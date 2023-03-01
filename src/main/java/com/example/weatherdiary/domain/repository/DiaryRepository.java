package com.example.weatherdiary.domain.repository;

import com.example.weatherdiary.domain.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary,Long> {
}
