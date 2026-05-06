package com.example.demo.repositories;

import com.example.demo.entities.FriendRequestEntity;
import com.example.demo.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {

    List<FriendRequestEntity> findAllByReceiver(UserEntity receiver);

    Optional<FriendRequestEntity> findById(Long id);

    Boolean existsBySenderAndReceiver(UserEntity sender, UserEntity receiver);

}
