package com.example.weatherdiary.controller;

import com.example.weatherdiary.service.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.BDDMockito.given;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

    @MockBean
    private DiaryService diaryService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createDiary() {
        // given
        // when
        // then
    }

}