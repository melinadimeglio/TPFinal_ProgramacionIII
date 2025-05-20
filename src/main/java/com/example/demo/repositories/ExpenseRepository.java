package com.example.demo.repositories;

import com.example.demo.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository <ExpenseEntity, Long> {
    List<ExpenseEntity> findByUserId(Long userId);
}
