package com.example.demo.services;

import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    public UserEntity findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontro el elemento"));
    }

    public void save(UserEntity user){
        if (userRepository.existsByDni(user.getDni())){
            throw new IllegalArgumentException("El DNI ya se encuentra regu¿istrado en el sistema.");
        }
        userRepository.save(user);
    }

    public void delete(UserEntity user){
        userRepository.delete(user);
    }
}
