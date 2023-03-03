package com.example.weatherdiary.controller;

import com.example.weatherdiary.domain.entity.Diary;
import com.example.weatherdiary.dto.CreateDiaryRequestDto;
import com.example.weatherdiary.dto.ModifyDiaryRequestDto;
import com.example.weatherdiary.service.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

    @MockBean
    private DiaryService diaryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("일기 저장 성공 테스트")
    void createDiaryTest() throws Exception {


        mockMvc.perform(post("/diary")
                       .contentType(MediaType.APPLICATION_JSON)
                       .param("date", String.valueOf(LocalDate.of(2023, 3, 3)))
                       .content(objectMapper.writeValueAsString(new CreateDiaryRequestDto(
                               "제목[테스트]", "내용[테스트]"
                       )))).andExpect(status().isOk())
               .andDo(print());

    }

    @Test
    @DisplayName("일기 조회 성공 테스트")
    void readDiaryTest() throws Exception {
        // given
        List<Diary> diaryList = new ArrayList<>();

        diaryList = Arrays.asList(Diary.builder()
                                       .id(2L)
                                       .weather("날씨[테스트]")
                                       .title("제목[테스트]")
                                       .text("내용[테스트]")
                                       .icon("아이콘[테스트]")
                                       .temperature(203.1)
                                       .date(LocalDate.of(2023, 3, 3))
                                       .build(),
                Diary.builder()
                     .id(3L)
                     .weather("날씨[테스트2]")
                     .title("제목[테스트]")
                     .text("내용[테스트]")
                     .icon("아이콘[테스트]")
                     .temperature(203.1)
                     .date(LocalDate.of(2023, 3, 3))
                     .build());


        given(diaryService.readDiary(any(LocalDate.class)))
                .willReturn(diaryList);


        // when
        mockMvc.perform(get("/diary")
                       .param("date", String.valueOf(LocalDate.of(2023, 3, 3))))
               .andExpect(jsonPath("$[0]").exists())
               .andExpect(jsonPath("$[2]").doesNotExist())
               .andExpect(jsonPath("$[1]").exists())
               .andExpect(jsonPath("$[1].icon").value("아이콘[테스트]"))
               .andExpect(jsonPath("$[0].weather").value("날씨[테스트]"))
               .andExpect(status().isOk())
               .andDo(print());
        // then
    }

    @Test
    @DisplayName("일기 수정 성공 테스트")
    void modifyDiaryTest() throws Exception {
        // given

        // when
        mockMvc.perform(put("/diary/1")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(
                               new ModifyDiaryRequestDto(
                                       "수정할 제목[테스트]", "수정할 내용[테스트]"
                               )
                       ))).andExpect(status().isOk())
                      .andDo(print());
        // then
    }

    @Test
    @DisplayName("일기 삭제 성공 테스트")
    void deleteDiaryTest() throws Exception {
        // given

        // when
        mockMvc.perform(delete("/diary/1"))
                .andExpect(status().isOk())
                .andDo(print());
        // then
     }

}