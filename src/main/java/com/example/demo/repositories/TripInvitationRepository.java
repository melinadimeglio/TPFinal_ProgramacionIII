package com.example.demo.repositories;

import com.example.demo.entities.TripEntity;
import com.example.demo.entities.TripInvitationEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripInvitationRepository extends JpaRepository <TripInvitationEntity, Long> {

    List<TripInvitationEntity> findAllByReceiverAndInvitationStatus(UserEntity receiver, RequestStatus status);
    boolean existsByReceiverAndTripAndInvitationStatus(
            UserEntity receiver,
            TripEntity trip,
            RequestStatus status
    );
    List<TripInvitationEntity> findAllByTrip(TripEntity trip);
    List<TripInvitationEntity> findAllBySenderAndTrip(UserEntity sender, TripEntity trip);

}
