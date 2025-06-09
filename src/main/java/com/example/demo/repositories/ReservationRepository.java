package com.example.demo.repositories;

import com.example.demo.entities.ReservationEntity;
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

}
