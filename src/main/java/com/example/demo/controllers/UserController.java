package com.example.demo.controllers;

import com.example.demo.DTOs.Trip.TripResponseDTO;
import com.example.demo.DTOs.User.UserCreateDTO;
import com.example.demo.DTOs.User.UserResponse;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.UserMapper;
import com.example.demo.services.UserService;
import io.swagger.v3.oas.annotations.Parameter;
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
    public ResponseEntity<UserResponse> getTripById(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toDTO(userService.findById(id)));
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
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @RequestBody @Valid UserUpdateDTO updatedUserDTO) {
        UserEntity existing = userService.findById(id);
        userMapper.updateUserEntityFromDTO(updatedUserDTO, existing);
        UserEntity saved = userService.save(existing);
        UserResponse response = userMapper.toDTO(saved);
        return ResponseEntity.ok(response);
    }


    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Long id) {

        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

