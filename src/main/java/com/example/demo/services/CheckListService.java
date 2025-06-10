package com.example.demo.services;

import com.example.demo.DTOs.CheckList.Request.CheckListCreateDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListUpdateDTO;
import com.example.demo.entities.CheckListEntity;
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

    public CheckListResponseDTO findById(Long id) {
        return checkListRepository.findById(id)
                .map(checkListMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Checklist no encontrada"));
    }

    public CheckListResponseDTO create(CheckListCreateDTO dto, Long myUserId) {
        if (dto.getTripId() == null) {
            throw new IllegalArgumentException("TripId no puede ser null.");
        }

        CheckListEntity entity = checkListMapper.toEntity(dto);
        entity.setTrip(tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("Viaje no encontrado")));

        entity.setUser(userRepository.findById(myUserId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado")));

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
        entity.setTrip(tripRepository.findById(dto.getTripId()).orElseThrow());
        entity.setUser(userRepository.findById(userId).orElseThrow());

        return checkListMapper.toDTO(checkListRepository.save(entity));
    }


    public void delete(Long id) {
        CheckListEntity checkListEntity = checkListRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontro la checklist con ID:" + id));
        checkListEntity.setActive(false);
    }

    public Page<CheckListResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return checkListRepository.findByUserId(userId, pageable)
                .map(checkListMapper::toDTO);
    }
}
