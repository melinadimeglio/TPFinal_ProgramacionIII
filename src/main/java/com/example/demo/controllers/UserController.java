package com.example.demo.controllers;

import com.example.demo.DTOs.User.UserCreateDTO;
import com.example.demo.DTOs.User.UserResponse;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.UserMapper;
import com.example.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserEntity user = userService.findById(id);
        UserResponse userResponse = userMapper.toDTO(user);
        return ResponseEntity.ok(userResponse);
    }

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateDTO user) {
        UserEntity userEntity = userMapper.toUserEntity(user);
        userService.save(userEntity);
        UserResponse response = userMapper.toDTO(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Actualizar un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id,
                                                 @RequestBody @Valid UserEntity updatedUser) {
        UserEntity existing = userService.findById(id);

        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        existing.setPassword(updatedUser.getPassword());
        existing.setDni(updatedUser.getDni());
        existing.setCategory(updatedUser.getCategory());
        existing.setPreferencias(updatedUser.getPreferencias());
        existing.setActive(updatedUser.isActive());
        existing.setTrips(updatedUser.getTrips());

        userService.save(existing);
        return ResponseEntity.ok(existing);
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        UserEntity user = userService.findById(id);
        userService.delete(user);
        return ResponseEntity.noContent().build();
    }
}

