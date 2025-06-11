package com.example.demo.repositories;

import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {
    Page<TripEntity> findByUsersId(Long userId, Pageable pageable);
    Page<TripEntity> findAllByActiveTrue(Pageable pageable);
    Page<TripEntity> findAllByActiveFalse(Pageable pageable);
    Page<TripEntity> findByDestinationContainsIgnoreCaseAndId(String destination, Long id, Pageable pageable);
    Page<TripEntity> findByStartDateAndId(LocalDate date, Long id, Pageable pageable);
    Page<TripEntity> findByDestinationContainsIgnoreCaseAndStartDateAndId(String destination, LocalDate date, Long id, Pageable pageable);
}
