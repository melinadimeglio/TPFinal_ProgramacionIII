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

    public static Specification<ActivityEntity> isAvailable() {
        return (root, query, cb) ->
                cb.isTrue(root.get("available"));
    }


    public static Specification<ActivityEntity> dateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate == null) return cb.lessThanOrEqualTo(root.get("date"), endDate);
            if (endDate == null) return cb.greaterThanOrEqualTo(root.get("date"), startDate);
            return cb.between(root.get("date"), startDate, endDate);
        };
    }

    public static Specification<ActivityEntity> priceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice == null) return cb.le(root.get("price"), maxPrice);
            if (maxPrice == null) return cb.ge(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    public static Specification<ActivityEntity> availableQuantityEquals(Long availableQuantity) {
        return (root, query, cb) -> {
            if (availableQuantity == null) return null;
            return cb.equal(root.get("available_quantity"), availableQuantity);
        };
    }

    public static Specification<ActivityEntity> hasCompany() {
        return (root, query, cb) -> cb.isNotNull(root.get("company"));
    }


    public static Specification<ActivityEntity> belongsToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;
            Join<Object, Object> join = root.join("users");
            return cb.equal(join.get("id"), userId);
        };
    }
}


