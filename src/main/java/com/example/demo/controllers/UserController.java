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
    private final UserMapper userMapper;
    private final UserModelAssembler assembler;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, UserModelAssembler assembler) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAll();

        return ResponseEntity.ok(assembler.toCollectionModel(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findById(id);

        return ResponseEntity.ok(assembler.toModel(user));
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
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO user) {
        UserResponseDTO responseDTO = userService.save(tripCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @RequestBody @Valid UserUpdateDTO updatedUserDTO) {
        UserEntity existing = userService.findById(id);
        userMapper.updateUserEntityFromDTO(updatedUserDTO, existing);
        UserEntity saved = userService.save(existing);
        UserResponseDTO response = userMapper.toDTO(saved);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/me")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        String username = authentication.getName();

        userService.deleteAccount(username);

        return ResponseEntity.ok("Cuenta eliminada correctamente");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(Authentication authentication) {
        String username = authentication.getName();

        UserResponseDTO profile = userService.getProfileByUsername(username);

        return ResponseEntity.ok(profile);
    }
}


