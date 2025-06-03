package com.example.demo.services;

import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.DTOs.User.Request.UserCreateDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.UserMapper;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.entities.RoleEntity;
import com.example.demo.security.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.security.repositories.CredentialRepository;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CredentialRepository credentialRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
                       PasswordEncoder passwordEncoder, CredentialRepository credentialRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.credentialRepository = credentialRepository;
    }

    public List<UserResponseDTO> findAll() {
        List<UserEntity> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    public UserResponseDTO findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con ID: " + id));
        return userMapper.toDTO(user);
    }

    public UserResponseDTO save(UserCreateDTO user) {
        UserEntity userEntity = userMapper.toUserEntity(user);
        UserEntity savedUser = userRepository.save(userEntity);

        CredentialEntity credential = new CredentialEntity();
        credential.setEmail(user.getEmail());
        credential.setPassword(passwordEncoder.encode(user.getPassword()));
        credential.setUser(savedUser); // Acá se asocia el user

        credentialRepository.save(credential);

        return userMapper.toDTO(savedUser);
    }


    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con ID: " + id));

        userMapper.updateUserEntityFromDTO(dto, existingUser);
        UserEntity savedUser = userRepository.save(existingUser);

        return userMapper.toDTO(savedUser);
    }

    public void delete(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con ID: " + id));
        userRepository.delete(user);
    }

    public void deleteAccount(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con username: " + username));
        user.setActive(false);
    }

    public UserResponseDTO getProfileByUsername(String username){
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con username: " + username));
        return userMapper.toDTO(user);
    }
}
