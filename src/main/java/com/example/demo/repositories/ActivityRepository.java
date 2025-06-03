package com.example.demo.repositories;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.enums.ActivityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository <ActivityEntity, Long> {
    List<ActivityEntity> findByUsers_Id(Long userId);
    List<ActivityEntity> findByCompanyId(Long companyId);


