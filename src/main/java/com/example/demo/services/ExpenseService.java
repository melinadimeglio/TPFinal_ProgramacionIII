package com.example.demo.services;

import com.example.demo.DTOs.Expense.Request.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.Response.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
import com.example.demo.entities.ExpenseEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.enums.ExpenseCategory;
import com.example.demo.mappers.ExpenseMapper;
import com.example.demo.repositories.ExpenseRepository;
import com.example.demo.repositories.TripRepository;
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
    private final TripRepository tripRepository;



    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, UserRepository userRepository, TripRepository tripRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;

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
        ExpenseEntity expense = expenseMapper.toEntity(dto);

        TripEntity trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("Viaje no encontrado con ID: " + dto.getTripId()));

        expense.setTrip(trip);

        if (expense.getUsers() == null || expense.getUsers().isEmpty()) {
            throw new IllegalStateException("No se puede dividir el gasto: no hay usuarios asignados.");
        }

        ExpenseEntity saved = expenseRepository.save(expense);

        double divided = saved.getAmount() / saved.getUsers().size();

        List<ExpenseEntity> expenses = expenseRepository.findByTripId(dto.getTripId());
        double total = expenses.stream().mapToDouble(ExpenseEntity::getAmount).sum();
        double estimated = trip.getEstimatedBudget();

        String budgetStatus = null;
        if (total > estimated) {
            budgetStatus = "⚠️ Se ha superado el presupuesto estimado.";
        } else if (total >= estimated * 0.5) {
            budgetStatus = "🔶 Se ha superado el 50% del presupuesto estimado.";
        } else if (total == estimated){
            budgetStatus = "❗ Has gastado todo el presupuesto disponible.";
        } else {
            budgetStatus = "✅ Presupuesto bajo control.";
        }
        ExpenseResponseDTO response = expenseMapper.toDTO(saved);
        response.setDividedAmount(divided);
        response.setBudgetWarning(budgetStatus);

        return response;
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
        entity.setActive(false);
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

    public Double getRealAverageExpenseByUser(Long userId) {
        List<ExpenseEntity> expenses = expenseRepository.findAll(); // o buscás los que incluyan al user

        double total = 0.0;
        int count = 0;

        for (ExpenseEntity expense : expenses) {
            if (expense.getUsers().stream().anyMatch(u -> u.getId().equals(userId))) {
                int sharedWith = expense.getUsers().size();
                if (sharedWith > 0) {
                    total += expense.getAmount() / sharedWith;
                    count++;
                }
            }
        }

        return count == 0 ? 0.0 : total / count;
    }

    public List<ExpenseResponseDTO> findByTripId(Long tripId) {
        List<ExpenseEntity> expenses = expenseRepository.findByTripId(tripId);
        return expenseMapper.toDTOList(expenses);
    }

    public Double getAverageExpenseByTripId(Long tripId) {
        List<ExpenseEntity> expenses = expenseRepository.findByTripId(tripId);

        if (expenses.isEmpty()) return 0.0;

        double total = expenses.stream().mapToDouble(ExpenseEntity::getAmount).sum();
        return total / expenses.size();
    }

    public Double getTotalExpenseByTripId(Long tripId) {
        List<ExpenseEntity> expenses = expenseRepository.findByTripId(tripId);

        return expenses.stream()
                .mapToDouble(ExpenseEntity::getAmount)
                .sum();
    }

    public Double getTotalRealExpenseByUser(Long userId) {
        List<ExpenseEntity> expenses = expenseRepository.findAll();

        return expenses.stream()
                .filter(expense -> expense.getUsers().stream().anyMatch(u -> u.getId().equals(userId)))
                .mapToDouble(expense -> expense.getAmount() / expense.getUsers().size())
                .sum();
    }

    public List<ExpenseResponseDTO> findByCategory(ExpenseCategory category) {
        List<ExpenseEntity> expenses = expenseRepository.findByCategory(category);
        return expenseMapper.toDTOList(expenses);
    }

}
