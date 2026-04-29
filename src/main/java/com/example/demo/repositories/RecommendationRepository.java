package com.example.demo.repositories;

import com.example.demo.entities.RecommendationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {

    Page<RecommendationEntity> findByTripId(Long tripId, Pageable pageable);
    Page<RecommendationEntity> findAllByActiveTrue(Pageable pageable);
    Page<RecommendationEntity> findAllByActiveFalse(Pageable pageable);

}
