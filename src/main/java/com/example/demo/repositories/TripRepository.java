package com.example.demo.repositories;

import com.example.demo.entities.TripEntity;
import com.example.demo.notifications.interfaces.TripBudget;
import com.example.demo.notifications.interfaces.TripReminder;
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
public interface TripRepository extends JpaRepository<TripEntity, Long>, JpaSpecificationExecutor<TripEntity> {
    Page<TripEntity> findByUsersId(Long userId, Pageable pageable);

    Page<TripEntity> findAllByActiveTrue(Pageable pageable);

    Page<TripEntity> findAllByActiveFalse(Pageable pageable);

    Page<TripEntity> findByDestinationContainsIgnoreCaseAndId(String destination, Long id, Pageable pageable);

    Page<TripEntity> findByStartDateAndId(LocalDate date, Long id, Pageable pageable);

    Page<TripEntity> findByDestinationContainsIgnoreCaseAndStartDateAndId(String destination, LocalDate date, Long id, Pageable pageable);

    @Query("SELECT t.id AS tripId, t.destination AS destination, u.id AS userId " +
            "FROM TripEntity t JOIN t.users u " +
            "WHERE t.startDate = :startDate AND t.active = true")
    List<TripReminder> findActiveTripsWithUsersByStartDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT t.id AS tripId, t.name AS tripName, t.estimatedBudget AS estimatedBudget, u.id AS userId " +
            "FROM TripEntity t JOIN t.users u " +
            "WHERE t.active = true AND t.estimatedBudget > 0")
    List<TripBudget> findActiveTripsWithBudgetAndUsers();
}
