package com.example.weatherdiary.controller;

import com.example.weatherdiary.service.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void createDiary() throws Exception {
        // given
        given(diaryService.createDiary(any(), anyString()))
                .willReturn("https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=aa9b8a3df6d399327837db0c3ea7dae5");

        LocalDate date = LocalDate.of(2023, 3, 1);
        // when
        mockMvc.perform(post("/diary")
                       .param("date", String.valueOf(date))
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(new String("안녕하세요"))))
               .andExpect(status().isOk())
               .andDo(print());
        // then
    }

}