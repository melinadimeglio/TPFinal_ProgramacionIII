package com.example.demo.SpecificationAPI;

import com.example.demo.entities.ExpenseEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.ExpenseCategory;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ExpenseSpecification {
    public static Specification<ExpenseEntity> belongsToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;
            Join<ExpenseEntity, UserEntity> join = root.join("users");
            return cb.equal(join.get("id"), userId);
        };
    }

    public static Specification<ExpenseEntity> hasCategory(ExpenseCategory category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<ExpenseEntity> amountBetween(Double minAmount, Double maxAmount) {
        return (root, query, cb) -> {
            if (minAmount == null && maxAmount == null) return null;
            if (minAmount == null) return cb.lessThanOrEqualTo(root.get("amount"), maxAmount);
            if (maxAmount == null) return cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
            return cb.between(root.get("amount"), minAmount, maxAmount);
        };
    }

    public static Specification<ExpenseEntity> dateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate == null) return cb.lessThanOrEqualTo(root.get("date"), endDate);
            if (endDate == null) return cb.greaterThanOrEqualTo(root.get("date"), startDate);
            return cb.between(root.get("date"), startDate, endDate);
        };
    }
}
