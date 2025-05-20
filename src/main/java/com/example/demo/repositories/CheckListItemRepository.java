package com.example.demo.repositories;
import com.example.demo.entities.CheckListItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckListItemRepository extends JpaRepository <CheckListItemEntity, Long> {

}
