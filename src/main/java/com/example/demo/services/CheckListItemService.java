package com.example.demo.services;

import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.CheckListItemEntity;
import com.example.demo.repositories.CheckListItemRepository;
import com.example.demo.repositories.CheckListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CheckListItemService {

    private final CheckListItemRepository checkListItemRepository;

    @Autowired
    public CheckListItemService(CheckListItemRepository checkListItemRepository) {
        this.checkListItemRepository = checkListItemRepository;
    }

    public List<CheckListItemEntity> findAll(){
        return checkListItemRepository.findAll();
    }

    public CheckListItemEntity findById(Long id){
        return checkListItemRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontro el elemento"));
    }

    public void save(CheckListItemEntity checkListItemEntity){
        checkListItemRepository.save(checkListItemEntity);
    }

    public void delete(CheckListItemEntity checkListItemEntity){
        checkListItemRepository.delete(checkListItemEntity);
    }

    public List<CheckListItemEntity> findByUserId(Long userId) {
        return checkListItemRepository.findByUserId(userId);
    }

}
