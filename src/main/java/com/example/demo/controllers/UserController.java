package com.example.demo.controllers;

import com.example.demo.DTOs.Trip.TripCreateDTO;
import com.example.demo.DTOs.Trip.TripResponseDTO;
import com.example.demo.DTOs.User.UserCreateDTO;
import com.example.demo.DTOs.User.UserResponse;
import com.example.demo.DTOs.User.UserUpdateDTO;
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

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getTripById(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toDTO(userService.findById(id)));
    }


    @Operation(
            summary = "Create a new trip",
            description = "This endpoint allows creating a new trip by providing destination, dates, estimated budget, number of companions, and a list of user IDs.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trip details to be created",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TripCreateDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Trip successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
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

