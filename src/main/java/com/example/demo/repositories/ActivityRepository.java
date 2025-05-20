package com.example.demo.repositories;

import com.example.demo.entities.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository <ActivityEntity, Long> {
}
