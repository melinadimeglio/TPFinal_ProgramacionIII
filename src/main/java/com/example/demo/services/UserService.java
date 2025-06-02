package com.example.demo.services;

import com.example.demo.DTOs.Trip.TripCreateDTO;
import com.example.demo.DTOs.Trip.TripResponseDTO;
import com.example.demo.DTOs.Trip.TripUpdateDTO;
import com.example.demo.DTOs.User.UserCreateDTO;
import com.example.demo.DTOs.User.UserResponse;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.UserMapper;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.entities.CredentialEntity;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public List<UserResponse> findAll() {
        List<UserEntity> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }


    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No user with id " + id));
    }

    public UserEntity save(UserEntity user) {
        CredentialEntity credential = new CredentialEntity();
        credential.setEmail(user.getEmail());
        credential.setPassword(passwordEncoder.encode(user.getPassword()));

        credential.setUser(user);
        user.setCredential(credential);
        return userRepository.save(user);
    }

    public void update(Long id, UserUpdateDTO dto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el viaje con ID " + id));

        userMapper.updateUserEntityFromDTO(dto, existingUser);
        userRepository.save(existingUser);
    }

    public void delete(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el viaje con ID " + id));
        userRepository.delete(user);
    }

    public void deleteAccount(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con username " + username));

        userRepository.delete(user);
    }


}


