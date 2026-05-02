package com.example.demo.repositories;

import com.example.demo.entities.NotificationEntity;
import com.example.demo.enums.NotificationCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<NotificationEntity> findByUserIdAndCategoryOrderByCreatedAtDesc(
            Long userId, NotificationCategory category, Pageable pageable);

    long countByUserIdAndReadFalse(Long userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.read = true, n.readAt = CURRENT_TIMESTAMP " +
            "WHERE n.userId = :userId AND n.read = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.read = true, n.readAt = CURRENT_TIMESTAMP " +
            "WHERE n.id = :id AND n.userId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);
}
