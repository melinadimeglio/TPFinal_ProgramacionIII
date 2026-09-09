package com.example.demo.repositories;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.enums.ActivityCategory;
import com.example.demo.notifications.interfaces.ActivityReminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long>, JpaSpecificationExecutor<ActivityEntity> {
    Page<ActivityEntity> findByUsers_Id(Long userId, Pageable pageable);

    Page<ActivityEntity> findByCompanyId(Long companyId, Pageable pageable);

    Page<ActivityEntity> findByCategory(ActivityCategory category, Pageable pageable);

    Page<ActivityEntity> findByDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    Page<ActivityEntity> findByCategoryAndDateBetween(ActivityCategory category, LocalDate start, LocalDate end, Pageable pageable);

    Page<ActivityEntity> findAllByAvailableTrue(Pageable pageable);

    Page<ActivityEntity> findAllByAvailableFalse(Pageable pageable);

    Page<ActivityEntity> findByCompanyIsNotNull(Pageable pageable);

    long countByUsers_Id(Long userId);

    @Query("SELECT a.id AS activityId, a.name AS activityName, a.date AS activityDate, u.id AS userId " +
            "FROM ActivityEntity a JOIN a.users u " +
            "WHERE a.available = true AND a.date = :targetDate")
    List<ActivityReminder> findActivitiesByDateWithUsers(@Param("targetDate") LocalDate targetDate);
}

