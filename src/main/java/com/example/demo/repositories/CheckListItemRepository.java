package com.example.demo.repositories;
import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.CheckListItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckListItemRepository extends JpaRepository <CheckListItemEntity, Long> {
    Page<CheckListItemEntity> findByChecklistIdAndStatus(Long checklistId, boolean status, Pageable pageable);
    Page<CheckListItemEntity> findByStatus(boolean status, Pageable pageable);
    List<CheckListItemEntity> findByChecklistId(Long checklistId);
    Page<CheckListItemEntity> findByChecklistUserId(Long userId, Pageable pageable);

}
