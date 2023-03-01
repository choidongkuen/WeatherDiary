package com.example.weatherdiary.exception;

public class NotFoundDiaryException extends RuntimeException{
    public NotFoundDiaryException(String message) {
        super(message);
    }
}
