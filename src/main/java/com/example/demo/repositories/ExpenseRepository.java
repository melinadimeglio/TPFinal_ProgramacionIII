package com.example.demo.repositories;

import com.example.demo.entities.ExpenseEntity;
import com.example.demo.enums.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository <ExpenseEntity, Long> {
    @Query("SELECT e FROM ExpenseEntity e JOIN e.users u WHERE u.id = :userId")
    List<ExpenseEntity> findByUserId(Long userId);
    List<ExpenseEntity> findByTripId(Long tripId);
    Page<ExpenseEntity> findByCategory(ExpenseCategory category, Pageable pageable);
}
