package com.example.demo.repositories;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.enums.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository <ActivityEntity, Long> {
    List<ActivityEntity> findByUsers_Id(Long userId);
    List<ActivityEntity> findByCompanyId(Long companyId);
    List<ActivityEntity> findByCategory(ActivityCategory category);
    List<ActivityEntity> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<ActivityEntity> findByCategoryAndStartTimeBetween(ActivityCategory category, LocalDateTime start, LocalDateTime end);
    }

