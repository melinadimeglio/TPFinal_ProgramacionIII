package com.example.demo.services;

import com.example.demo.DTOs.Trip.TripInvitationDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.TripInvitationEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.RequestStatus;
import com.example.demo.repositories.TripInvitationRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.repositories.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TripInvitationService {

    private final CredentialRepository credentialRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final TripInvitationRepository tripInvitationRepository;
    private final TripRepository tripRepository;

    @Autowired
    public TripInvitationService(CredentialRepository credentialRepository,
                                 UserService userService,
                                 UserRepository userRepository,
                                 NotificationService notificationService,
                                 TripInvitationRepository tripInvitationRepository,
                                 TripRepository tripRepository) {
        this.credentialRepository = credentialRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.tripInvitationRepository = tripInvitationRepository;
        this.tripRepository = tripRepository;
    }

    public void sendInvitation(String senderEmail, Long receiverId, Long tripId) {
        UserEntity sender = getLoggedUser(senderEmail);
        UserEntity receiver = userService.findByIdAdmin(receiverId);
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("Trip not found: " + tripId));

        boolean isFriend = sender.getFriends().stream()
                .anyMatch(f -> f.getId().equals(receiverId));
        if (!isFriend) {
            throw new IllegalArgumentException("You can only invite friends to a trip.");
        }

        boolean belongsToTrip = trip.getUsers().stream()
                .anyMatch(u -> u.getId().equals(sender.getId()));
        if (!belongsToTrip) {
            throw new AccessDeniedException("You are not part of this trip.");
        }

        if (tripInvitationRepository.existsByReceiverAndTrip(receiver, trip)) {
            throw new IllegalStateException("An invitation has already been sent to this user for this trip.");
        }

        TripInvitationEntity invitation = TripInvitationEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .trip(trip)
                .invitationStatus(RequestStatus.PENDING)
                .build();

        tripInvitationRepository.save(invitation);
        notificationService.notifyTripInvite(
                receiver.getId(),
                trip.getDestination(),
                sender.getUsername(),
                invitation.getId(),
                trip.getId()
        );
    }

    public Long acceptInvitation(String email, Long invitationId) {
        UserEntity receiver = getLoggedUser(email);
        TripInvitationEntity invitation = findAndValidate(invitationId, receiver);

        invitation.setInvitationStatus(RequestStatus.ACCEPTED);

        TripEntity trip = invitation.getTrip();
        trip.getUsers().add(receiver);
        receiver.getTrips().add(trip);

        tripInvitationRepository.save(invitation);
        userRepository.save(receiver);

        notificationService.notifyTripInvitationAccepted(
                invitation.getSender().getId(),
                receiver.getUsername(),
                trip.getId()
        );

        return trip.getId();
    }

    public void denyInvitation(String email, Long invitationId) {
        UserEntity receiver = getLoggedUser(email);
        TripInvitationEntity invitation = findAndValidate(invitationId, receiver);
        tripInvitationRepository.delete(invitation);
    }

    public List<TripInvitationDTO> getPendingInvitations(String email) {
        UserEntity receiver = getLoggedUser(email);
        return tripInvitationRepository.findAllByReceiverAndInvitationStatus(receiver, RequestStatus.PENDING)
                .stream()
                .map(i -> new TripInvitationDTO(
                        i.getId(),
                        i.getSender().getUsername(),
                        i.getReceiver().getId(),
                        i.getTrip().getId(),
                        i.getTrip().getDestination(),
                        i.getInvitationStatus()
                ))
                .toList();
    }

    private TripInvitationEntity findAndValidate(Long invitationId, UserEntity receiver) {
        TripInvitationEntity invitation = tripInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new NoSuchElementException("Invitation not found."));

        if (!invitation.getReceiver().getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("You do not have permission to respond to this invitation.");
        }

        if (!invitation.getInvitationStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalStateException("The invitation has already been processed.");
        }

        return invitation;
    }

    private UserEntity getLoggedUser(String email) {
        return credentialRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                .getUser();
    }

    public List<TripInvitationDTO> getSentInvitations(String email, Long tripId) {
        UserEntity sender = getLoggedUser(email);
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("Trip not found: " + tripId));

        return tripInvitationRepository.findAllBySenderAndTrip(sender, trip)
                .stream()
                .map(i -> new TripInvitationDTO(
                        i.getId(),
                        i.getSender().getUsername(),
                        i.getReceiver().getId(),
                        i.getTrip().getId(),
                        i.getTrip().getDestination(),
                        i.getInvitationStatus()
                ))
                .toList();
    }

}
