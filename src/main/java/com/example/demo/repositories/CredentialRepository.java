package com.example.demo.repositories;

import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository <CredentialEntity, Long> {
}
