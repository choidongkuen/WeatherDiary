package com.example.weatherdiary.dto;

import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DateInfo {

    @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "시작 날짜")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "끝 날짜")
    private LocalDate endDate;
}
