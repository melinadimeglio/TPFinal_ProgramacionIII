package com.example.demo.services;

import com.example.demo.DTOs.Filter.ExpenseFilterDTO;
import com.example.demo.DTOs.Expense.Request.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.Response.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
import com.example.demo.DTOs.Expense.Response.ExpenseResumeDTO;
import com.example.demo.SpecificationAPI.ExpenseSpecification;
import com.example.demo.entities.ExpenseEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.ExpenseCategory;
import com.example.demo.exceptions.ReservationException;
import com.example.demo.mappers.ExpenseMapper;
import com.example.demo.repositories.ExpenseRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Page<ExpenseResponseDTO> findAll(Pageable pageable){
        return expenseRepository.findAllByActiveTrue(pageable)
                .map(expenseMapper::toDTO);
    }

    public Page<ExpenseResponseDTO> findAllInactive(Pageable pageable){
        return expenseRepository.findAllByActiveFalse(pageable)
                .map(expenseMapper::toDTO);
    }

    public ExpenseResponseDTO findById(Long id) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expense not found."));
        return expenseMapper.toDTO(entity);
    }


    public ExpenseResponseDTO save(ExpenseCreateDTO dto, Long myUserId) {

        TripEntity trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("Trip not found: " + dto.getTripId()));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to record expenses on this trip.");
        }

        Set<UserEntity> users = new HashSet<>();

        UserEntity owner = userRepository.findById(myUserId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        users.add(owner);

        if (dto.getSharedUserIds() != null && !dto.getSharedUserIds().isEmpty()) {
            for (Long sharedId : dto.getSharedUserIds()) {
                UserEntity sharedUser = userRepository.findById(sharedId)
                        .orElseThrow(() -> new RuntimeException("Shared user not found."));

                if (sharedUser.getCredential().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
                    throw new ReservationException("You cannot add Admin type users.");
                }
                users.add(sharedUser);
            }
        }

        Set<UserEntity> usersTrip = trip.getUsers();

        if (!usersTrip.equals(users)){
            throw new ReservationException("The shared users must exactly match the users in the trip.");
        }

        if (dto.getDate().isBefore(trip.getStartDate()) || dto.getDate().isAfter(trip.getEndDate())) {
            throw new ReservationException("The expense date must be within the trip's start and end dates.");
        }

        ExpenseEntity expense = expenseMapper.toEntity(dto);
        expense.setTrip(trip);
        expense.setUsers(users);

        if (users.isEmpty()) {
            throw new IllegalStateException("Cannot split expense: no users assigned.");
        }

        ExpenseEntity saved = expenseRepository.save(expense);

        double divided = saved.getAmount() / saved.getUsers().size();

        List<ExpenseEntity> expenses = expenseRepository.findByTripId(dto.getTripId());
        double total = expenses.stream().mapToDouble(ExpenseEntity::getAmount).sum();
        double estimated = trip.getEstimatedBudget();

        String budgetStatus;
        if (total > estimated) {
            budgetStatus = "⚠️ The estimated budget has been exceeded.";
        } else if (total >= estimated * 0.5) {
            budgetStatus = "🔶 The estimated budget has been exceeded by 50%.";
        } else if (total == estimated){
            budgetStatus = "❗ You have spent all the available budget.";
        } else {
            budgetStatus = "✅ Budget under control.";
        }

        ExpenseResponseDTO response = expenseMapper.toDTO(saved);
        response.setDividedAmount(divided);
        response.setBudgetWarning(budgetStatus);

        return response;
    }

    public void updateIfOwned(Long id, ExpenseUpdateDTO dto, Long myUserId) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expense not found."));

        TripEntity trip = entity.getTrip();

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You do not have permission to modify this expense.");
        }

        if (dto.getDate().isBefore(trip.getStartDate()) || dto.getDate().isAfter(trip.getEndDate())) {
            throw new ReservationException("The expense date must be within the trip's start and end dates.");
        }

        expenseMapper.updateEntityFromDTO(dto, entity);
        expenseRepository.save(entity);
    }

    public void deleteIfOwned(Long id, Long myUserId) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expense not found."));

        TripEntity trip = entity.getTrip();

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You do not have permission to delete this expense.");
        }

        entity.setActive(false);
        expenseRepository.save(entity);
    }


    public void restoreIfOwned(Long id, Long myUserId) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expense not found."));

        TripEntity trip = entity.getTrip();

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You do not have permission to restore this expense.");
        }

        entity.setActive(true);
        expenseRepository.save(entity);
    }


    public Page<ExpenseResumeDTO> findByUserId(Long userId, Pageable pageable) {
        return expenseRepository.findByUserId(userId, pageable)
                .map(expenseMapper::toResumeDTO);
    }

    public Double getAverageExpenseByUserId(Long id) {
        List<ExpenseEntity> expenses = expenseRepository.findByUserId(id);
        if (expenses.isEmpty()) return 0.0;
        double sum = expenses.stream().mapToDouble(ExpenseEntity::getAmount).sum();
        return sum / expenses.size();
    }

    public Double getRealAverageExpenseByUser(Long userId) {
        List<ExpenseEntity> expenses = expenseRepository.findAll();

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

    public Page<ExpenseResumeDTO> findByTripIdIfOwned(Long tripId, Long myUserId, Pageable pageable) {

        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("Trip not found."));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to view expenses for this trip.");
        }

        return expenseRepository.findByTripId(tripId, pageable)
                .map(expenseMapper::toResumeDTO);
    }


    public Double getAverageExpenseByTripIdIfOwned(Long tripId, Long myUserId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("Trip not found."));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to view expenses for this trip.");
        }

        List<ExpenseEntity> expenses = expenseRepository.findByTripId(tripId);

        if (expenses.isEmpty()) return 0.0;

        double total = expenses.stream().mapToDouble(ExpenseEntity::getAmount).sum();
        return total / expenses.size();
    }

    public Double getTotalExpenseByTripIdIfOwned(Long tripId, Long myUserId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("Trip not found."));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to view expenses for this trip.");
        }

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

    public Page<ExpenseResponseDTO> findByCategory(ExpenseCategory category, Pageable pageable) {
        return expenseRepository.findByCategory(category, pageable)
                .map(expenseMapper::toDTO);
    }

    public ExpenseResumeDTO findResumeById(Long id) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expense not found."));
        return expenseMapper.toResumeDTO(entity);
    }

    public Page<ExpenseResumeDTO> findByUserIdWithFilters(Long userId, ExpenseFilterDTO filters, Pageable pageable) {

        Specification<ExpenseEntity> spec = Specification
                .where(ExpenseSpecification.belongsToUser(userId))
                .and(ExpenseSpecification.hasCategory(filters.getCategory()))
                .and(ExpenseSpecification.amountBetween(filters.getMinAmount(), filters.getMaxAmount()))
                .and(ExpenseSpecification.dateBetween(filters.getStartDate(), filters.getEndDate()));

        Page<ExpenseEntity> result = expenseRepository.findAll(spec, pageable);
        return result.map(expenseMapper::toResumeDTO);
    }


}
