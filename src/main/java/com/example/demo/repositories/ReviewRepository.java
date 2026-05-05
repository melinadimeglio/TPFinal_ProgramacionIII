package com.example.demo.repositories;

import com.example.demo.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByActivityIdOrderByFechaCreacionDesc(Long activityId);

    boolean existsByUserIdAndActivityId(Long userId, Long activityId);

    Optional<ReviewEntity> findByUserIdAndActivityId(Long userId, Long activityId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.activity.id = :activityId")
    Double findAverageRatingByActivityId(@Param("activityId") Long activityId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.activity.id = :activityId")
    Long countByActivityId(@Param("activityId") Long activityId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.activity.id = :activityId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> findRatingDistributionByActivityId(@Param("activityId") Long activityId);
}

