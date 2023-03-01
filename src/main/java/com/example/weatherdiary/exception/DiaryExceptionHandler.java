package com.example.weatherdiary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DiaryExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> NotFoundDiaryExceptionHandler(
            NotFoundDiaryException exception
    ) {

        return ResponseEntity.badRequest()
                .body(ErrorMessage.of(exception,HttpStatus.BAD_REQUEST));

    }
}
