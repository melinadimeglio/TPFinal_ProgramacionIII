package com.example.demo.repositories;

import com.example.demo.entities.ExpenseEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository <ExpenseEntity, Long>, JpaSpecificationExecutor<ExpenseEntity> {
    @Query("SELECT e FROM ExpenseEntity e JOIN e.users u WHERE u.id = :userId")
    Page<ExpenseEntity> findByUserId(Long userId, Pageable pageable);
    @Query("SELECT e FROM ExpenseEntity e JOIN e.users u WHERE u.id = :userId")
    List<ExpenseEntity> findByUserId(Long userId);
    List<ExpenseEntity> findByUsers_IdAndActiveTrue(Long userId);
    Page<ExpenseEntity> findByTripId(Long tripId, Pageable pageable);
    List<ExpenseEntity> findByTripId(Long tripId);
    List<ExpenseEntity> findByTripIdAndActiveTrue(Long tripId);
    Page<ExpenseEntity> findByCategory(ExpenseCategory category, Pageable pageable);
    Page<ExpenseEntity> findAllByActiveTrue(Pageable pageable);
    List<ExpenseEntity> findAllByActiveTrue();
    Page<ExpenseEntity> findAllByActiveFalse(Pageable pageable);
}
