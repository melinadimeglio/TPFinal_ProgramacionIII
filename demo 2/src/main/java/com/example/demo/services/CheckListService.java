package com.example.demo.services;

import com.example.demo.repositories.CheckListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckListService {
    private final CheckListRepository checkListRepository;

    @Autowired
    public CheckListService(CheckListRepository checkListRepository) {
        this.checkListRepository = checkListRepository;
    }
}
