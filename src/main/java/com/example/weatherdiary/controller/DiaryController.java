package com.example.weatherdiary.controller;

import com.example.weatherdiary.domain.entity.Diary;
import com.example.weatherdiary.dto.CreateDiaryRequestDto;
import com.example.weatherdiary.dto.DateInfo;
import com.example.weatherdiary.dto.ModifyDiaryRequestDto;
import com.example.weatherdiary.service.DiaryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping()
    public ResponseEntity createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody CreateDiaryRequestDto request// JSON 형식 그대로 들어오기 때문에 MAP 으로 받기
    ) throws IOException {
        diaryService.createDiary(date, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<Diary>> readDiary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return new ResponseEntity<>(
                diaryService.readDiary(date), HttpStatus.OK);

    }

    @GetMapping("/between")
    public ResponseEntity<List<Diary>> readDiaries(
            @ModelAttribute DateInfo request
    ) {
        return new ResponseEntity<>(
                diaryService.readDiaries(request), HttpStatus.OK
        );
    }

    @PutMapping("/{diaryId}")
    public ResponseEntity modifiyDiary(
            @RequestBody ModifyDiaryRequestDto request,
            @PathVariable("diaryId") Long id
    ) {
        diaryService.modifyDiary(request, id);
        return ResponseEntity.ok().build();
    }
}
