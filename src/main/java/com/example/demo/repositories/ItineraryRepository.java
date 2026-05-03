package com.example.demo.repositories;

import com.example.demo.entities.ItineraryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItineraryRepository extends JpaRepository<ItineraryEntity, Long>, JpaSpecificationExecutor<ItineraryEntity> {
    Page<ItineraryEntity> findByUserId(Long userId, Pageable pageable);

    Page<ItineraryEntity> findAllByActiveTrue(Pageable pageable);

    Page<ItineraryEntity> findAllByActiveFalse(Pageable pageable);

    @Query("SELECT i FROM ItineraryEntity i LEFT JOIN FETCH i.activities WHERE i.id = :id")
    Optional<ItineraryEntity> findByIdWithActivities(@Param("id") Long id);

}
