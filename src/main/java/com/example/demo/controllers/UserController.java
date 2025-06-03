package com.example.demo.controllers;

import com.example.demo.DTOs.Trip.Request.TripCreateDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.DTOs.User.Request.UserCreateDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.controllers.hateoas.UserModelAssembler;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.UserMapper;
import com.example.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;

    @Autowired
    public UserController(UserService userService, UserModelAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAll();
        return ResponseEntity.ok(assembler.toCollectionModel(users));
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findById(id);
        return ResponseEntity.ok(assembler.toModel(user));
    }

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDTO>> createUser(@RequestBody @Valid UserCreateDTO user) {
        UserResponseDTO responseDTO = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(responseDTO));
    }

    // Actualizar un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> updateUser(@PathVariable Long id,
                                                                   @RequestBody @Valid UserUpdateDTO updatedUserDTO) {
        UserResponseDTO response = userService.update(id, updatedUserDTO);
        return ResponseEntity.ok(assembler.toModel(response));
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar tu propia cuenta (usuario autenticado)
    @DeleteMapping("/me/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteAccount(username);
        return ResponseEntity.ok("Cuenta eliminada correctamente");
    }

    // Obtener tu perfil (usuario autenticado)
    @GetMapping("/me")
    public ResponseEntity<EntityModel<UserResponseDTO>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponseDTO profile = userService.getProfileByUsername(username);
        return ResponseEntity.ok(assembler.toModel(profile));
    }
}