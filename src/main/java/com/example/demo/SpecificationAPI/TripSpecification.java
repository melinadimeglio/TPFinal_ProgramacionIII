package com.example.demo.SpecificationAPI;

import com.example.demo.entities.TripEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TripSpecification {
    public static Specification<TripEntity> hasDestination(String destination) {
        return (root, query, cb) -> {
            if (destination == null || destination.isBlank()) return null;
            return cb.like(cb.lower(root.get("destination")), "%" + destination.toLowerCase() + "%");
        };
    }

    public static Specification<TripEntity> startDateAfterOrEqual(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) return null;
            return cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
        };
    }

    public static Specification<TripEntity> endDateBeforeOrEqual(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) return null;
            return cb.lessThanOrEqualTo(root.get("endDate"), endDate);
        };
    }

    public static Specification<TripEntity> belongsToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;
            return cb.equal(root.join("users").get("id"), userId);
        };
    }
}
