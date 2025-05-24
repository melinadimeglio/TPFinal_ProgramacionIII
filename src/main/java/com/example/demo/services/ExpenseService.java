package com.example.demo.services;

import com.example.demo.DTOs.Expense.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
import com.example.demo.entities.ExpenseEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.ExpenseMapper;
import com.example.demo.repositories.ExpenseRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExpenseService{
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final UserRepository userRepository;


    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.userRepository = userRepository;

    }

    public List<ExpenseResponseDTO> findAll(){
        List<ExpenseEntity> entities = expenseRepository.findAll();
        return expenseMapper.toDTOList(entities);
    }

    public ExpenseResponseDTO findById(Long id) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el gasto"));
        return expenseMapper.toDTO(entity);
    }


    public ExpenseResponseDTO save(ExpenseCreateDTO dto) {
        ExpenseEntity entity = expenseMapper.toEntity(dto);

        if (dto.getUserId() != null) {
            UserEntity user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + dto.getUserId()));
            entity.setUser(user);
        }

        ExpenseEntity savedEntity = expenseRepository.save(entity);
        return expenseMapper.toDTO(savedEntity);
    }


    public void update(Long id, ExpenseUpdateDTO dto) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el gasto"));
        expenseMapper.updateEntityFromDTO(dto, entity);
        expenseRepository.save(entity);
    }

    public void delete(Long id) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el gasto"));
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
