package com.example.weatherdiary.controller;

import com.example.weatherdiary.domain.entity.Diary;
import com.example.weatherdiary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping()
    public ResponseEntity createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody Map<String, String> text // JSON 형식 그대로 들어오기 때문에 MAP 으로 받
    ) throws IOException {
        diaryService.createDiary(date,text.get("text"));
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<Diary>> getDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        return new ResponseEntity<>(
                diaryService.readDiary(date), HttpStatus.OK);

    }

}
