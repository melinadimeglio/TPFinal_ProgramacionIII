package com.example.demo.services;

import com.example.demo.DTOs.CheckList.CheckListItemCreateDTO;
import com.example.demo.DTOs.CheckList.CheckListItemResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListItemUpdateDTO;
import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.CheckListItemEntity;
import com.example.demo.mappers.CheckListItemMapper;
import com.example.demo.repositories.CheckListItemRepository;
import com.example.demo.repositories.CheckListRepository;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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
        CheckListItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));

        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.isStatus());

        CheckListEntity checklist = checkListRepository.findById(dto.getChecklistId())
                .orElseThrow(() -> new NoSuchElementException("Checklist no encontrada"));
        entity.setChecklist(checklist);

        return itemMapper.toDTO(itemRepository.save(entity));
    }


    public void delete(Long id) {
        CheckListItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));
        itemRepository.delete(entity);
    }
}
