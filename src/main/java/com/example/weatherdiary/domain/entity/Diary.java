package com.example.weatherdiary.domain.entity;

import com.example.weatherdiary.dto.ModifyDiaryRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
@Where(clause = "DELETED_AT is null")
@SQLDelete(sql = "UPDATE diary SET diary.DELETED_AT = CURRENT_TIMESTAMP WHERE diary.id = ?")
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

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private LocalDate date;

    public void update(ModifyDiaryRequestDto request) {
        this.title = request.getTitle();
        this.text = request.getText();
    }
}
