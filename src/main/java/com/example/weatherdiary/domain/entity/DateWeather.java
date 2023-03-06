package com.example.weatherdiary.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE date_weather set date_weather.deleted_at = CURRENT_TIMESTAMP WHERE date_weather.id = ?")
@Table
@Entity
public class DateWeather extends BaseEntity {

    @Id
    private LocalDate date;

    @Column(name = "weather")
    private String weather;

    @Column(name = "icon")
    private String icon;

    @Column(name = "temperature")
    private Double temperature;

    public static DateWeather of(Map<String, Object> parsedWeather) {
        return DateWeather.builder()
                          .weather(parsedWeather.get("main").toString())
                          .icon(parsedWeather.get("icon").toString())
                          .temperature((Double) parsedWeather.get("temp"))
                          .date(LocalDate.now())
                          .build();
    }
}
