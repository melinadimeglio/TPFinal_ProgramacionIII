package com.example.demo.security.repositories;

import com.example.demo.security.entities.PermitEntity;
import com.example.demo.security.enums.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermitRepository extends JpaRepository<PermitEntity, Long> {
    Optional<PermitEntity> findByPermit(Permit permit);
}
