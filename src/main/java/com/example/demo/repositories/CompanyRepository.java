package com.example.demo.repositories;

import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTaxId (String taxId);
    Page<CompanyEntity> findAllByActiveTrue(Pageable pageable);
    Page<CompanyEntity> findAllByActiveFalse(Pageable pageable);

}
