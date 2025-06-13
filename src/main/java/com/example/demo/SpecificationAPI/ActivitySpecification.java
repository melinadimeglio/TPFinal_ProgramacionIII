package com.example.demo.SpecificationAPI;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.enums.ActivityCategory;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ActivitySpecification {
    public static Specification<ActivityEntity> hasCategory(ActivityCategory category) {
        return (root, query, cb) -> {
            if (category == null) return null;
            return cb.equal(root.get("category"), category);
        };
    }

    public static Specification<ActivityEntity> dateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate == null) return cb.lessThanOrEqualTo(root.get("date"), endDate);
            if (endDate == null) return cb.greaterThanOrEqualTo(root.get("date"), startDate);
            return cb.between(root.get("date"), startDate, endDate);
        };
    }

    public static Specification<ActivityEntity> belongsToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;
            Join<Object, Object> join = root.join("users");
            return cb.equal(join.get("id"), userId);
        };
    }
}


