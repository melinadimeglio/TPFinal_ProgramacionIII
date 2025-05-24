package com.example.demo.services;

import com.example.demo.entities.CheckListEntity;
import com.example.demo.repositories.CheckListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CheckListService {
    private final CheckListRepository checkListRepository;

    @Autowired
    public CheckListService(CheckListRepository checkListRepository) {
        this.checkListRepository = checkListRepository;
    }

    public List<CheckListEntity> findAll(){
        return checkListRepository.findAll();
    }

    public CheckListEntity findById(Long id){
        return checkListRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontro el elemento"));
    }

    public void save(CheckListEntity checkListEntity){
        checkListRepository.save(checkListEntity);
    }

    public void delete(CheckListEntity checkListEntity){
        checkListRepository.delete(checkListEntity);
    }

    public List<CheckListEntity> findByUserId(Long userId) {
        return checkListRepository.findByUserId(userId);
    }
}
