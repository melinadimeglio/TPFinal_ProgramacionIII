package com.example.demo.services;

import com.example.demo.DTOs.CheckList.CheckListCreateDTO;
import com.example.demo.DTOs.CheckList.CheckListResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListUpdateDTO;
import com.example.demo.entities.CheckListEntity;
import com.example.demo.mappers.CheckListMapper;
import com.example.demo.repositories.CheckListRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CheckListService {
    private final CheckListRepository checkListRepository;
    private final CheckListMapper checkListMapper;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Autowired
    public CheckListService(CheckListRepository checkListRepository,
                            CheckListMapper checkListMapper,
                            TripRepository tripRepository,
                            UserRepository userRepository) {
        this.checkListRepository = checkListRepository;
        this.checkListMapper = checkListMapper;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public List<CheckListResponseDTO> findAll() {
        return checkListMapper.toDTOList(checkListRepository.findAll());
    }

    public CheckListResponseDTO findById(Long id) {
        return checkListRepository.findById(id)
                .map(checkListMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Checklist no encontrada"));
    }

    public CheckListResponseDTO create(CheckListCreateDTO dto) {
        if (dto.getTripId() == null || dto.getUserId() == null) {
            throw new IllegalArgumentException("TripId y UserId no pueden ser null.");
        }

        CheckListEntity entity = checkListMapper.toEntity(dto);
        entity.setTrip(tripRepository.findById(dto.getTripId()).orElseThrow(() -> new NoSuchElementException("Viaje no encontrado")));
        entity.setUser(userRepository.findById(dto.getUserId()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado")));
        entity.setCompleted(false);

        return checkListMapper.toDTO(checkListRepository.save(entity));
    }
    public CheckListResponseDTO update(Long id, CheckListUpdateDTO dto) {
        CheckListEntity entity = checkListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Checklist no encontrada"));

        checkListMapper.updateEntityFromDTO(dto, entity);
        entity.setTrip(tripRepository.findById(dto.getTripId()).orElseThrow());
        entity.setUser(userRepository.findById(dto.getUserId()).orElseThrow());

        return checkListMapper.toDTO(checkListRepository.save(entity));
    }

    public void delete(Long id) {
        checkListRepository.deleteById(id);
    }

    public List<CheckListResponseDTO> findByUserId(Long userId) {
        return checkListMapper.toDTOList(checkListRepository.findByUserId(userId));
    }
}
