package com.example.demo.SpecificationAPI;

import com.example.demo.entities.CheckListEntity;
import org.springframework.data.jpa.domain.Specification;

public class CheckListSpecification {
    public static Specification<CheckListEntity> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<CheckListEntity> hasCompleted(Boolean completed) {
        return (root, query, cb) -> cb.equal(root.get("completed"), completed);
    }
}
