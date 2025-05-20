package com.example.demo.services;

import com.example.demo.entities.ExpenseEntity;
import com.example.demo.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExpenseService{
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseEntity> findAll(){
        return expenseRepository.findAll();
    }

    public ExpenseEntity findById(Long id){
        return expenseRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontro el elemento"));
    }

    public void save(ExpenseEntity expenseEntity){
        expenseRepository.save(expenseEntity);
    }

    public void delete(ExpenseEntity expenseEntity){
        expenseRepository.delete(expenseEntity);
    }

    public Double getAverageExpenseById(Long id){
        List<ExpenseEntity> expenses = expenseRepository.findByUserId(id);
        if (expenses.isEmpty()) return 0.0;
        double sum = expenses.stream().mapToDouble(ExpenseEntity::getAmount).sum();
        return sum / expenses.size();
    }

    public Double getAverageExpense() {
        List<ExpenseEntity> expenses = expenseRepository.findAll();
        if (expenses.isEmpty()) return 0.0;

        double sum = expenses.stream().mapToDouble(ExpenseEntity::getAmount).sum();
        return sum / expenses.size();
    }
}
