package com.example.demo.services;

import com.example.demo.DTOs.CheckList.Request.CheckListItemCreateDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListItemUpdateDTO;
import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.CheckListItemEntity;
import com.example.demo.mappers.CheckListItemMapper;
import com.example.demo.repositories.CheckListItemRepository;
import com.example.demo.repositories.CheckListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CheckListItemService {

    private final CheckListItemRepository itemRepository;
    private final CheckListItemMapper itemMapper;
    private final CheckListRepository checkListRepository;


    public List<CheckListItemResponseDTO> findAll() {
        return itemMapper.toDTOList(itemRepository.findAll());
    }

    public CheckListItemResponseDTO findById(Long id) {
        CheckListItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));
        return itemMapper.toDTO(entity);
    }


    public CheckListItemResponseDTO create(CheckListItemCreateDTO dto) {

        CheckListItemEntity entity = itemMapper.toEntity(dto);
        CheckListEntity checklist = checkListRepository.findById(dto.getChecklistId())
                .orElseThrow(() -> new NoSuchElementException("Checklist no encontrada"));
        if (checklist.getUser() == null) {
            throw new IllegalStateException("La checklist no tiene un usuario asignado.");
        }
        entity.setChecklist(checklist);
        entity.setStatus(false);
        return itemMapper.toDTO(itemRepository.save(entity));
    }

    public CheckListItemResponseDTO update(Long id, CheckListItemUpdateDTO dto) {
        // Buscar el ítem existente
        CheckListItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));

        // Mapear los campos desde el DTO, ignorando nulls y checklist
        itemMapper.updateEntityFromDTO(dto, entity);

        // Asignar la checklist manualmente
        CheckListEntity checklist = checkListRepository.findById(dto.getChecklistId())
                .orElseThrow(() -> new NoSuchElementException("Checklist no encontrada"));

        entity.setChecklist(checklist);

        // Guardar y devolver como DTO
        CheckListItemEntity updated = itemRepository.save(entity);
        return itemMapper.toDTO(updated);
    }


    public void delete(Long id) {
        CheckListItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));
        itemRepository.delete(entity);
    }

    public List<CheckListItemResponseDTO> findByChecklistAndStatus(Long checklistId, boolean completed) {
        List<CheckListItemEntity> items = itemRepository.findByChecklistIdAndStatus(checklistId, completed);
        return items.stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<CheckListItemResponseDTO> findByStatus(boolean completed) {
        List<CheckListItemEntity> items = itemRepository.findByStatus(completed);
        return items.stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }


}
