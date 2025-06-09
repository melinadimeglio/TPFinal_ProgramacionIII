package com.example.demo.repositories;

import com.example.demo.entities.CategoryEntity;
import com.example.demo.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository <CategoryEntity, Long> {
    Optional<CategoryEntity> findByName(String name);
    boolean existsByName(String name);
    Page<CategoryEntity> findAllByActiveTrue(Pageable pageable);
    Page<CategoryEntity> findAllByActiveFalse(Pageable pageable);
}
