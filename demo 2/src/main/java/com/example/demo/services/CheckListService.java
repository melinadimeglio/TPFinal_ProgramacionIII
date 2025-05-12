package com.example.demo.services;

import com.example.demo.entities.CheckListEntity;
import com.example.demo.repositories.CheckListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
