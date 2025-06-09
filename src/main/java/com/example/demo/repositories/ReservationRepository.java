package com.example.demo.repositories;

import com.example.demo.entities.ReservationEntity;
import com.example.demo.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByUserId(Long userId);
    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.activity a " +
            "JOIN a.company c " +
            "WHERE c.id = :companyId")
    List<ReservationEntity> findByCompanyId(Long companyId);
    Page<ReservationEntity> findAllByActiveTrue(Pageable pageable);
    Page<ReservationEntity> findAllByActiveFalse(Pageable pageable);
    Page<ReservationEntity> findByUserId(Long userId, Pageable pageable);
    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.activity a " +
            "JOIN a.company c " +
            "WHERE c.id = :companyId")
    Page<ReservationEntity> findByCompanyId(Long companyId, Pageable pageable);
}
