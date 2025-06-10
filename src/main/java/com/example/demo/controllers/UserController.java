package com.example.demo.controllers;

import com.example.demo.DTOs.User.Request.UserCreateDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.controllers.hateoas.UserModelAssembler;
import com.example.demo.entities.UserEntity;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;
    private final PagedResourcesAssembler<UserResponseDTO> pagedResourcesAssembler;

    @Autowired
    public UserController(UserService userService, UserModelAssembler assembler, PagedResourcesAssembler<UserResponseDTO> pagedResourcesAssembler) {
        this.userService = userService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all registered users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)))
    })

    @PreAuthorize("hasAuthority('VER_TODOS_USUARIOS')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserResponseDTO>>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> usersPage = userService.findAll(pageable);
        PagedModel<EntityModel<UserResponseDTO>> model = pagedResourcesAssembler.toModel(usersPage, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Get user by ID", description = "Returns a specific user by ID if authorized.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to access this resource"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    //@PreAuthorize("hasAuthority('VER_TODOS_USUARIOS')") ¿cual va aca?
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponseDTO user = userService.findById(id);
        return ResponseEntity.ok(assembler.toModel(user));
    }

    @Operation(summary = "Create a new user", description = "Creates a new user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDTO>> createUser(@RequestBody @Valid UserCreateDTO user) {
        UserResponseDTO responseDTO = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(responseDTO));
    }

    @Operation(summary = "Update user by ID", description = "Updates the details of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasAuthority('MODIFICAR_USUARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO updatedUserDTO) {
        UserResponseDTO response = userService.update(id, updatedUserDTO);
        return ResponseEntity.ok(assembler.toModel(response));
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a specific user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAuthority('ELIMINAR_USUARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete own account", description = "Deletes the authenticated user's account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAuthority('ELIMINAR_USUARIO')")
    @DeleteMapping("/me/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteAccount(username);
        return ResponseEntity.ok("Cuenta eliminada correctamente");
    }

    @Operation(summary = "Get own profile", description = "Retrieves the authenticated user's profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<EntityModel<UserResponseDTO>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponseDTO profile = userService.getProfileByUsername(username);
        return ResponseEntity.ok(assembler.toModel(profile));
    }

    //solo para hateoas
    @GetMapping("/public/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserByIdPublic(@PathVariable Long id) {
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('ASIGNAR_ROLES')")
    @PutMapping("/roles/{id}/")
    public ResponseEntity<UserEntity> assignRole(@PathVariable Long id,
                                                 @RequestBody String role){
        UserEntity user = userService.assignRole(id, role);
        userService.update(user);

        return ResponseEntity.ok(user);
    }
}
