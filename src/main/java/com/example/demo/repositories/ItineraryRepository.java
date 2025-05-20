package com.example.demo.repositories;

import com.example.demo.controllers.ItineraryController;
import com.example.demo.entities.ItineraryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryRepository extends JpaRepository<ItineraryEntity, Long> {
}
