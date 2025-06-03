package com.example.demo.repositories;

import com.example.demo.security.entities.PermitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitRepository extends JpaRepository<PermitEntity, Long> {
}
