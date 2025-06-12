package com.example.demo.services;

import com.example.demo.DTOs.CheckList.Request.CheckListCreateDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListUpdateDTO;
import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.CheckListItemEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.CheckListMapper;
import com.example.demo.repositories.CheckListItemRepository;
import com.example.demo.repositories.CheckListRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CheckListService {
    private final CheckListRepository checkListRepository;
    private final CheckListMapper checkListMapper;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final CheckListItemRepository checkListItemRepository;

    @Autowired
    public CheckListService(CheckListRepository checkListRepository,
                            CheckListMapper checkListMapper,
                            TripRepository tripRepository,
                            UserRepository userRepository, CheckListItemRepository checkListItemRepository) {
        this.checkListRepository = checkListRepository;
        this.checkListMapper = checkListMapper;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.checkListItemRepository = checkListItemRepository;
    }

    public Page<CheckListResponseDTO> findAll(Pageable pageable) {
        return checkListRepository.findAllByActiveTrue(pageable)
                .map(checkListMapper::toDTO);
    }

    public Page<CheckListResponseDTO> findAllInactive(Pageable pageable) {
        return checkListRepository.findAllByActiveFalse(pageable)
                .map(checkListMapper::toDTO);
    }

    public CheckListResponseDTO findByIdIfOwned(Long id, Long myUserId) {
        CheckListEntity entity = checkListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Checklist no encontrada"));

        if (!entity.getUser().getId().equals(myUserId)) {
            throw new AccessDeniedException("No tienes permiso para ver este checklist");
        }

        return checkListMapper.toDTO(entity);
    }

    public CheckListResponseDTO create(CheckListCreateDTO dto, Long myUserId) {
        if (dto.getTripId() == null) {
            throw new IllegalArgumentException("TripId no puede ser null.");
        }

        TripEntity trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("Viaje no encontrado"));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("No tienes permiso para crear checklists en este viaje");
        }

        UserEntity user = userRepository.findById(myUserId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        CheckListEntity entity = checkListMapper.toEntity(dto);
        entity.setTrip(trip);
        entity.setUser(user);
        entity.setCompleted(false);

        return checkListMapper.toDTO(checkListRepository.save(entity));
    }


    public CheckListResponseDTO update(Long id, CheckListUpdateDTO dto, Long userId) {

        CheckListEntity entity = checkListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Checklist no encontrada"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tenés permiso para modificar esta checklist.");
        }

        checkListMapper.updateEntityFromDTO(dto, entity);

        TripEntity trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("Viaje no encontrado"));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!belongsToUser) {
            throw new AccessDeniedException("No tienes permiso para asignar esta checklist a ese viaje");
        }

        entity.setTrip(trip);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        entity.setUser(user);

        if (dto.getCompleted() != null) {
            entity.setCompleted(dto.getCompleted());

            List<CheckListItemEntity> items = checkListItemRepository.findByChecklistId(entity.getId());
            for (CheckListItemEntity item : items) {
                item.setStatus(dto.getCompleted());
            }
            checkListItemRepository.saveAll(items);
        }

        return checkListMapper.toDTO(checkListRepository.save(entity));
    }


    public void deleteIfOwned(Long id, Long userId) {
        CheckListEntity checkListEntity = checkListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró la checklist con ID: " + id));

        if (!checkListEntity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar esta checklist.");
        }

        checkListEntity.setActive(false);
        checkListRepository.save(checkListEntity);
    }


    public void restoreIfOwned(Long id, Long userId) {
        CheckListEntity checkListEntity = checkListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró la checklist con ID: " + id));

        if (!checkListEntity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permiso para restaurar esta checklist.");
        }

        checkListEntity.setActive(true);
        checkListRepository.save(checkListEntity);
    }


    public Page<CheckListResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return checkListRepository.findByUserId(userId, pageable)
                .map(checkListMapper::toDTO);
    }
}
