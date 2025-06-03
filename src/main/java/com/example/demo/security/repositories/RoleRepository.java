package com.example.demo.security.repositories;

import com.example.demo.security.entities.RoleEntity;
import com.example.demo.security.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(Role role);
}
