package com.example.demo.services;

import com.example.demo.DTOs.Expense.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
import com.example.demo.entities.ExpenseEntity;
import com.example.demo.mappers.ExpenseMapper;
import com.example.demo.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExpenseService{
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    public List<ExpenseResponseDTO> findAll(){
        List<ExpenseEntity> entities = expenseRepository.findAll();
        return expenseMapper.toDTOList(entities);
    }

    public ExpenseResponseDTO findById(Long id){
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el gasto"));
        return expenseMapper.toDTO(entity);
    }

    public ExpenseEntity getEntityById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el gasto"));
    }

    public void save(ExpenseCreateDTO dto){
        ExpenseEntity entity = expenseMapper.toEntity(dto);
        expenseRepository.save(entity);
    }


    public void update(Long id, ExpenseUpdateDTO dto) {
        ExpenseEntity entity = getEntityById(id);
        expenseMapper.updateEntityFromDTO(dto, entity);
        expenseRepository.save(entity);
    }

    public void delete(Long id) {
        ExpenseEntity entity = getEntityById(id);
        expenseRepository.delete(entity);
    }

    public List<ExpenseResponseDTO> findByUserId(Long userId) {
        List<ExpenseEntity> expenses = expenseRepository.findByUserId(userId);
        return expenseMapper.toDTOList(expenses);
    }



    public Double getAverageExpenseByUserId(Long id) {
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
