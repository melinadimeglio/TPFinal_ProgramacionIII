package com.example.demo.repositories;

import com.example.demo.entities.RecommendationEntity;
import com.example.demo.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {

    Page<RecommendationEntity> findByTripId(Long tripId, Pageable pageable);
    Page<RecommendationEntity> findAllByActiveTrue(Pageable pageable);
    Page<RecommendationEntity> findAllByActiveFalse(Pageable pageable);

}
