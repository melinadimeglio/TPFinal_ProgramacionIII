package com.example.demo.services;

import com.example.demo.DTOs.Filter.TripFilterDTO;
import com.example.demo.DTOs.Trip.Request.TripCreateDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.DTOs.Trip.TripUpdateDTO;
import com.example.demo.SpecificationAPI.TripSpecification;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.ReservationException;
import com.example.demo.mappers.TripMapper;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripMapper tripMapper;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public TripService(TripRepository tripRepository, UserRepository userRepository, TripMapper tripMapper, CloudinaryService cloudinaryService) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripMapper = tripMapper;
        this.cloudinaryService = cloudinaryService;
    }

    public Page<TripResponseDTO> findAll(Pageable pageable) {
        return tripRepository.findAllByActiveTrue(pageable)
                .map(tripMapper::toDTO);
    }

    public Page<TripResponseDTO> findAllInactive(Pageable pageable) {
        return tripRepository.findAllByActiveFalse(pageable)
                .map(tripMapper::toDTO);
    }

    public TripResponseDTO findById(Long id) {
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trip not found: " + id));
        return tripMapper.toDTO(trip);
    }


    public TripResponseDTO findByIdForUser(Long tripId, Long userId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to access this trip");
        }

        return tripMapper.toDTO(trip);
    }


    public TripResponseDTO save(TripCreateDTO dto, Long myUserId, MultipartFile file) {

        Set<UserEntity> users = new HashSet<>();

        UserEntity owner = userRepository.findById(myUserId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        users.add(owner);

        if (dto.getSharedUserIds() != null && !dto.getSharedUserIds().isEmpty()) {
            for (Long sharedId : dto.getSharedUserIds()) {
                UserEntity sharedUser = userRepository.findById(sharedId)
                        .orElseThrow(() -> new RuntimeException("Shared user not found."));

                if (sharedUser.getCredential().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    throw new ReservationException("You cannot add Admin type users.");
                } else if (sharedUser.getCredential().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_COMPANY"))) {
                    throw new ReservationException("You cannot add Company type users.");
                }
                users.add(sharedUser);
            }
        }

        if (dto.getCompanions() == null) {
            dto.setCompanions(0);
        }

        if (dto.getCompanions() != null && dto.getCompanions() != users.size() - 1) {
            throw new ReservationException("The number of companions does not match the number of users sharing the trip.");
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("The end date cannot be earlier than the start date.");
        }

        TripEntity trip = tripMapper.toEntity(dto);
        trip.setUsers(users);

        for (UserEntity user : users) {
            user.getTrips().add(trip);
        }

        TripEntity savedTrip = tripRepository.save(trip);

        if (file != null && !file.isEmpty()) {
            String url = cloudinaryService.uploadImage(file);
            savedTrip.setImageUrl(url);
            tripRepository.save(savedTrip);
        }

        return tripMapper.toDTO(savedTrip);
    }

    @Transactional
    public TripResponseDTO updateIfBelongsToUser(Long tripId, TripUpdateDTO dto, Long userId, MultipartFile file) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to modify this trip");
        }

        Set<UserEntity> newUsers = new HashSet<>();
        UserEntity owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        newUsers.add(owner);

        if (dto.getSharedUserIds() != null && !dto.getSharedUserIds().isEmpty()) {
            for (Long sharedId : dto.getSharedUserIds()) {
                UserEntity sharedUser = userRepository.findById(sharedId)
                        .orElseThrow(() -> new RuntimeException("Shared user not found."));
                newUsers.add(sharedUser);
            }
        }

        if (dto.getCompanions() != null && dto.getCompanions() != newUsers.size() - 1) {
            throw new ReservationException("The number of companions does not match the number of users sharing the trip.");
        }

        tripMapper.updateEntityFromDTO(dto, trip);

        Set<UserEntity> previousUsers = trip.getUsers() == null ? new HashSet<>() : new HashSet<>(trip.getUsers());

        trip.setUsers(newUsers);
        for (UserEntity u : newUsers) {
            if (u.getTrips() == null) u.setTrips(new HashSet<>());
            u.getTrips().add(trip);
        }

        for (UserEntity removed : previousUsers) {
            if (!newUsers.contains(removed)) {
                removed.getTrips().remove(trip);
            }
        }

        TripEntity updated = tripRepository.save(trip);

        if (file != null && !file.isEmpty()) {
            if (updated.getImageUrl() != null) {
                cloudinaryService.deleteImage(updated.getImageUrl());
            }
            String url = cloudinaryService.uploadImage(file);
            updated.setImageUrl(url);
            tripRepository.save(updated);
        }

        return tripMapper.toDTO(updated);
    }

    public void softDeleteIfBelongsToUser(Long tripId, Long userId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to delete this trip");
        }

        trip.setActive(false);
        trip.getChecklist().forEach(checkListEntity -> checkListEntity.setActive(false));
        trip.getItineraries().forEach(itineraryEntity -> itineraryEntity.setActive(false));

        tripRepository.save(trip);
    }

    public void restoreIfBelongsToUser(Long tripId, Long userId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to restore this trip");
        }

        trip.setActive(true);
        trip.getChecklist().forEach(checkListEntity -> checkListEntity.setActive(true));
        trip.getItineraries().forEach(itineraryEntity -> itineraryEntity.setActive(true));

        tripRepository.save(trip);
    }

    public Page<TripResponseDTO> findByUserId(Long userId, String destination, LocalDate date, Pageable pageable) {
        Page<TripEntity> tripEntity;

        if (destination != null && date != null) {
            tripEntity = tripRepository.findByDestinationContainsIgnoreCaseAndStartDateAndId(destination, date, userId, pageable);
        } else if (destination != null) {
            tripEntity = tripRepository.findByDestinationContainsIgnoreCaseAndId(destination, userId, pageable);
        } else if (date != null) {
            tripEntity = tripRepository.findByStartDateAndId(date, userId, pageable);
        } else {
            tripEntity = tripRepository.findByUsersId(userId, pageable);
        }
        return tripEntity.map(tripMapper::toDTO);
    }

    public TripEntity getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    public Page<TripResponseDTO> findByUserIdWithFilters(Long userId, TripFilterDTO filters, Pageable pageable) {
        Specification<TripEntity> spec = Specification
                .where(TripSpecification.belongsToUser(userId))
                .and(TripSpecification.hasDestination(filters.getDestination()))
                .and(TripSpecification.startDateAfterOrEqual(filters.getStartDate()))
                .and(TripSpecification.endDateBeforeOrEqual(filters.getEndDate()));

        Page<TripEntity> result = tripRepository.findAll(spec, pageable);
        return result.map(tripMapper::toDTO);
    }

    public List<TripResponseDTO> getFriendTrips(Long friendId, Long myId) {
        UserEntity user = userRepository.findById(myId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + myId));

        boolean isFriend = user.getFriends().stream()
                .anyMatch(f -> f.getId().equals(friendId));

        if (!isFriend) {
            throw new IllegalArgumentException("This user is not your friend.");
        }

        UserEntity friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + friendId));

        return friend.getTrips().stream()
                .map(tripMapper::toDTO)
                .toList();
    }
}


