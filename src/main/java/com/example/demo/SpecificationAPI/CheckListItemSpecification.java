package com.example.demo.SpecificationAPI;

import com.example.demo.entities.CheckListItemEntity;
import org.springframework.data.jpa.domain.Specification;

public class CheckListItemSpecification {
    public static Specification<CheckListItemEntity> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("checklist").get("user").get("id"), userId);
    }

    public static Specification<CheckListItemEntity> hasChecklistId(Long checklistId) {
        return (root, query, cb) -> cb.equal(root.get("checklist").get("id"), checklistId);
    }

    public static Specification<CheckListItemEntity> hasStatus(Boolean status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
