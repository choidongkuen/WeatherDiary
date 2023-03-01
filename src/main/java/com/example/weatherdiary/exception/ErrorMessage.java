package com.example.weatherdiary.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ErrorMessage {

    private int statusCode;
    private String errorSimpleName;
    private String msg;
    private LocalDateTime timestamp;

    public ErrorMessage(Exception exception, HttpStatus status) {
        this.statusCode = status.value();
        this.errorSimpleName = exception.getClass().getSimpleName();
        this.msg = exception.getMessage();
        this.timestamp = LocalDateTime.now();
    }
    public static ErrorMessage of(Exception exception, HttpStatus badRequest) {
        return new ErrorMessage(exception,badRequest);
    }
}
