package com.example.demo.services;

import com.example.demo.DTOs.Activity.CompanyActivityUpdateDTO;
import com.example.demo.DTOs.Activity.Response.ActivityCreateResponseDTO;
import com.example.demo.DTOs.Expense.Request.ExpenseCreateDTO;
import com.example.demo.DTOs.Filter.ActivityFilterDTO;
import com.example.demo.DTOs.Activity.Request.CompanyActivityCreateDTO;
import com.example.demo.DTOs.Activity.Request.UserActivityCreateDTO;
import com.example.demo.DTOs.Activity.Response.ActivityCompanyResponseDTO;
import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Activity.ActivityUpdateDTO;

import com.example.demo.SpecificationAPI.ActivitySpecification;
import com.example.demo.entities.*;
import com.example.demo.enums.ActivityCategory;
import com.example.demo.enums.ExpenseCategory;
import com.example.demo.exceptions.ReservationException;
import com.example.demo.mappers.ActivityMapper;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.ItineraryRepository;
import com.example.demo.repositories.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ItineraryRepository itineraryRepository;
    private final ItineraryService itineraryService;
    private final ExpenseService expenseService;
    private final TripService tripService;

    @Autowired
    public ActivityService(ActivityRepository activityRepository,
                           ActivityMapper activityMapper,
                           UserRepository userRepository,
                           CompanyRepository companyRepository,
                           ItineraryRepository itineraryRepository, ItineraryService itineraryService, ExpenseService expenseService, TripService tripService) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.itineraryRepository = itineraryRepository;
        this.itineraryService = itineraryService;
        this.expenseService = expenseService;
        this.tripService = tripService;
    }

    public ActivityCreateResponseDTO createFromUser(UserActivityCreateDTO dto, Long myUserId, Long itineraryId) {
        ActivityEntity entity = activityMapper.toEntity(dto);
        entity.setAvailable(true);

        ItineraryEntity itinerary = null;

        if (itineraryId != null) {
            itinerary = itineraryRepository.findById(itineraryId)
                    .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

            if (!itinerary.getUser().getId().equals(myUserId)) {
                throw new AccessDeniedException("You are not allowed to add activities to this itinerary.");
            }

            entity.setItinerary(itinerary);
        }

        TripEntity trip = tripService.getTripById(itinerary.getTrip().getId());

        Set<UserEntity> users = new HashSet<>();

        UserEntity owner = userRepository.findById(myUserId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        users.add(owner);

        if (dto.getSharedUserIds() != null && !dto.getSharedUserIds().isEmpty()) {
            for (Long sharedId : dto.getSharedUserIds()) {
                UserEntity sharedUser = userRepository.findById(sharedId)
                        .orElseThrow(() -> new RuntimeException("Shared user not found."));

                if (sharedUser.getCredential().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
                    throw new ReservationException("You cannot add Admin type users.");
                }
                users.add(sharedUser);
            }
        }

        if (trip != null){
            Set<UserEntity> usersTrip = trip.getUsers();

            if (users.size() > 1 && !usersTrip.containsAll(users)) {
                throw new ReservationException("Some shared users are not part of the trip.");
            }
        }

        entity.setUsers(users);

        ActivityEntity saved = activityRepository.save(entity);
        if (!itineraryService.addActivity(itineraryId, myUserId, saved.getId())){
            throw new ReservationException("The activity could not be created.");
        }

        Set<Long> usersIds = users.stream()
                        .map(UserEntity::getId)
                                .collect(Collectors.toSet());

        expenseService.save(ExpenseCreateDTO.builder()
                .amount(dto.getPrice())
                .description(dto.getDescription())
                .date(dto.getDate())
                .tripId(itinerary.getTrip().getId())
                .category(ExpenseCategory.ACTIVIDADES)
                .sharedUserIds(usersIds)
                .build(), myUserId);

        return activityMapper.toDTOCreated(saved);
    }

    public boolean updateCapacity (Long activityId, int quantity){

        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("Activity not found."));

        if (activity.getAvailable_quantity() != null && activity.getAvailable_quantity()-quantity >= 0){
            activity.setAvailable_quantity(activity.getAvailable_quantity() - quantity);
            activityRepository.save(activity);
            return true;
        }else {
            return false;
        }
    }

    public ActivityCompanyResponseDTO createFromCompanyService(CompanyActivityCreateDTO dto, Long companyId) {

        System.out.println("ID DE COMPANY DENTRO DE CREATE FROM COMPANY: " + companyId);

        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("Company not found"));

        ActivityEntity entity = activityMapper.toEntity(dto, company);

        entity.setAvailable(true);

        ActivityEntity saved = activityRepository.save(entity);
        return activityMapper.toCompanyResponseDTO(saved);
    }

    public ActivityResponseDTO findById(Long id) {
        ActivityEntity entity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity not found."));
        return activityMapper.toDTO(entity);
    }

    public Page<ActivityCompanyResponseDTO> findByCompanyId(Long companyId, Pageable pageable) {
        return activityRepository.findByCompanyId(companyId, pageable)
                .map(activityMapper::toCompanyResponseDTO);
    }


    public Page<ActivityResponseDTO> findAll(Pageable pageable) {
        return activityRepository.findAllByAvailableTrue(pageable)
                .map(activityMapper::toDTO);
    }

    public Page<ActivityResponseDTO> findAllCompany (Pageable pageable){
        Page<ActivityEntity> activities = activityRepository.findAllByAvailableTrue(pageable);
        List<ActivityEntity> activitiesList = activities.stream()
                .toList();

        List<ActivityEntity> activitiesFiltered = activitiesList.stream()
                .filter(activityEntity -> activityEntity.getCompany() != null)
                .toList();

        Page<ActivityEntity> page = new PageImpl<>(activitiesFiltered, pageable, activitiesFiltered.size());

        return page.map(activityMapper::toDTO);
    }

    public Page<ActivityResponseDTO> findAllInactive(Pageable pageable) {
        return activityRepository.findAllByAvailableFalse(pageable)
                .map(activityMapper::toDTO);
    }

    public Page<ActivityResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return activityRepository.findByUsers_Id(userId, pageable)
                .map(activityMapper::toDTO);
    }

    public ActivityCreateResponseDTO updateAndReturnIfOwned(Long id, ActivityUpdateDTO dto, Long myUserId) {
        ActivityEntity entity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity not found."));

        if (entity.getItinerary() != null) {
            if (!entity.getItinerary().getUser().getId().equals(myUserId)) {
                throw new AccessDeniedException("You do not have permission to modify this activity.");
            }
        } else {
            boolean belongsToUser = entity.getUsers().stream()
                    .anyMatch(user -> user.getId().equals(myUserId));
            if (!belongsToUser) {
                throw new AccessDeniedException("You do not have permission to modify this activity.");
            }
        }

        ItineraryEntity newItinerary = null;

        if (dto.getItineraryId() != null) {
            newItinerary = itineraryRepository.findById(dto.getItineraryId())
                    .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

            if (!newItinerary.getUser().getId().equals(myUserId)) {
                throw new AccessDeniedException("You do not have permission to move the activity to that itinerary.");
            }

            if (!dto.getDate().equals(newItinerary.getItineraryDate())) {
                throw new IllegalArgumentException("The activity date must match the itinerary date.");
            }

            entity.setItinerary(newItinerary);
        }

        if (dto.getItineraryId() == null || dto.getItineraryId().equals(entity.getItinerary() != null ? entity.getItinerary().getId() : null)) {
            if (entity.getItinerary() != null) {
                if (!entity.getItinerary().getUser().getId().equals(myUserId)) {
                    throw new AccessDeniedException("You do not have permission to modify activities in this itinerary.");
                }
            } else {
                boolean belongsToUser = entity.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(myUserId));
                if (!belongsToUser) {
                    throw new AccessDeniedException("You do not have permission to modify this activity.");
                }
            }
        }

        activityMapper.updateEntityFromDTO(dto, entity);

        if (entity.getStartTime() != null && entity.getEndTime() != null &&
                entity.getEndTime().isBefore(entity.getStartTime())) {
            throw new IllegalArgumentException("The end time must be after the start time.");
        }

        ActivityEntity saved = activityRepository.save(entity);

        return activityMapper.toDTOCreated(saved);
    }


    public void deleteIfOwned(Long id, Long myUserId) {
        ActivityEntity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity not found."));

        if (activity.getItinerary() != null) {
            ItineraryEntity itinerary = activity.getItinerary();
            if (!itinerary.getUser().getId().equals(myUserId)) {
                throw new AccessDeniedException("You do not have permission to delete this activity.");
            }
        } else {
            boolean belongsToUser = activity.getUsers().stream()
                    .anyMatch(user -> user.getId().equals(myUserId));
            if (!belongsToUser) {
                throw new AccessDeniedException("You do not have permission to delete this activity.");
            }
        }

        activity.setAvailable(false);
        activityRepository.save(activity);
    }

    public void restoreIfOwned(Long id, Long myUserId) {
        ActivityEntity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity not found."));

        if (activity.getItinerary() != null) {
            ItineraryEntity itinerary = activity.getItinerary();
            if (!itinerary.getUser().getId().equals(myUserId)) {
                throw new AccessDeniedException("You do not have permission to restore this activity (you do not own the itinerary).");
            }
        }
        else {
            boolean belongsToUser = activity.getUsers().stream()
                    .anyMatch(user -> user.getId().equals(myUserId));
            if (!belongsToUser) {
                throw new AccessDeniedException("You do not have permission to restore this activity.");
            }
        }

        activity.setAvailable(true);
        activityRepository.save(activity);
    }

    public ActivityResponseDTO updateActivityByCompany(Long companyId, Long activityId, CompanyActivityUpdateDTO dto) {
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("Activity not found."));

        if (activity.getCompany() == null || !activity.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("You do not have permission to modify this activity.");
        }

        activityMapper.updateEntityFromCompanyDTO(dto, activity);
        activityRepository.save(activity);

        return activityMapper.toDTO(activity);
    }

    public void deleteActivityByCompany(Long companyId, Long activityId) {
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("Activity not found."));

        if (activity.getCompany() == null || !activity.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("You do not have permission to delete this activity.");
        }

        activity.setAvailable(false);
        activityRepository.save(activity);
    }

    public void restoreActivityByCompany(Long companyId, Long activityId) {
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("Activity not found."));

        if (activity.getCompany() == null || !activity.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("You do not have permission to restore this activity.");
        }

        activity.setAvailable(true);
        activityRepository.save(activity);
    }

    public Page<ActivityCreateResponseDTO> findByUserIdWithFilters(Long userId, ActivityFilterDTO filters, Pageable pageable) {

        Specification<ActivityEntity> spec = Specification
                .where(ActivitySpecification.belongsToUser(userId))
                .and(ActivitySpecification.hasCategory(filters.getCategory()))
                .and(ActivitySpecification.isAvailable())
                .and(ActivitySpecification.dateBetween(filters.getStartDate(), filters.getEndDate()));

        Page<ActivityEntity> result = activityRepository.findAll(spec, pageable);
        return result.map(activityMapper::toDTOCreated);
    }

    public Page<ActivityCompanyResponseDTO> findAllCompany(
            ActivityCategory category,
            LocalDate startDate,
            LocalDate endDate,
            Double minPrice,
            Double maxPrice,
            Long availableQuantity,
            Pageable pageable) {

        Specification<ActivityEntity> spec = Specification
                .where(ActivitySpecification.hasCategory(category))
                .and(ActivitySpecification.dateBetween(startDate, endDate))
                .and(ActivitySpecification.priceBetween(minPrice, maxPrice))
                .and(ActivitySpecification.availableQuantityEquals(availableQuantity))
                .and(ActivitySpecification.hasCompany());  // solo trae las de empresas

        Page<ActivityEntity> result = activityRepository.findAll(spec, pageable);
        return result.map(activityMapper::toCompanyResponseDTO);
    }



}
