package com.example.weatherdiary.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ModifyDiaryRequestDto {

    private String title;
    private String text;
}
