package com.example.demo.repositories;

import com.example.demo.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, Long> {
    boolean existsByDni (String dni);
    Optional<UserEntity> findByUsername(String username);
    Page<UserEntity> findAllByActiveTrue(Pageable pageable);
    Page<UserEntity> findAllByActiveFalse(Pageable pageable);

}
