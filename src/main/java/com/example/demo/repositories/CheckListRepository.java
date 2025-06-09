package com.example.demo.repositories;

import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckListRepository extends JpaRepository <CheckListEntity, Long> {
    Page<CheckListEntity> findByUserId(Long userId, Pageable pageable);
    Page<CheckListEntity> findAllByActiveTrue(Pageable pageable);
    Page<CheckListEntity> findAllByActiveFalse(Pageable pageable);


}
