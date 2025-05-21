package com.example.demo.repositories;
import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.CheckListItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckListItemRepository extends JpaRepository <CheckListItemEntity, Long> {
    List<CheckListItemEntity> findByUserId(Long userId);

}
