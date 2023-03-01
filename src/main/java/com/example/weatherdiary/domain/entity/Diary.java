package com.example.weatherdiary.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Diary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "weather")
    private String weather;

    @Column(name = "icon")
    private String icon;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private LocalDate date;

}
