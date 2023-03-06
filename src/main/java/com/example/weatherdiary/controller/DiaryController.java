package com.example.weatherdiary.controller;

import com.example.weatherdiary.domain.entity.Diary;
import com.example.weatherdiary.dto.CreateDiaryRequestDto;
import com.example.weatherdiary.dto.DateInfo;
import com.example.weatherdiary.dto.ModifyDiaryRequestDto;
import com.example.weatherdiary.service.DiaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation(value = "일기 생성 Api") // api 한줄 소개
    @PostMapping()
    public ResponseEntity createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody CreateDiaryRequestDto request// JSON 형식 그대로 들어오기 때문에 MAP 으로 받기
    ) throws IOException {
        diaryService.createDiary(date, request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "특정 날짜 일기 조회 Api")
    @GetMapping()
    public ResponseEntity<List<Diary>> readDiary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")
            @ApiParam(value = "날짜 형식 : yyyy-MM-dd",example = "2020-02-03") LocalDate date
    ) {
        return new ResponseEntity<>(
                diaryService.readDiary(date), HttpStatus.OK);

    }


    @ApiOperation(value = "두 기간 사이의 일기 조회 Api")
    @GetMapping("/between")
    public ResponseEntity<List<Diary>> readDiaries(
            @ModelAttribute DateInfo request
    ) {
        return new ResponseEntity<>(
                diaryService.readDiaries(request), HttpStatus.OK
        );
    }

    @ApiOperation(value = "일기 수정 Api")
    @PutMapping("/{diaryId}")
    public ResponseEntity modifyDiary(
            @RequestBody ModifyDiaryRequestDto request,
            @PathVariable("diaryId") Long id
    ) {
        diaryService.modifyDiary(request, id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "일기 삭제 Api")
    @DeleteMapping("{diaryId}")
    public ResponseEntity deleteDiary(
            @PathVariable("diaryId") Long id
    ) {
        diaryService.deleteDiary(id);
        return ResponseEntity.ok().build();
    }
}
