package com.example.demo.controllers;

import com.example.demo.DTOs.GlobalError.ErrorResponseDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.DTOs.User.Request.UserCreateDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.controllers.hateoas.UserModelAssembler;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.OwnershipException;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Operations related to user management")
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


    @Operation(
            summary = "Get all users",
            description = "Returns a list of all registered users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('VER_TODOS_USUARIOS')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserResponseDTO>>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> usersPage = userService.findAll(pageable);
        PagedModel<EntityModel<UserResponseDTO>> model = pagedResourcesAssembler.toModel(usersPage, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get all inactive users",
            description = "Retrieves a paginated list of all inactive (soft-deleted) users. Requires the 'VER_TODOS_USUARIOS' authority."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inactive users retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('VER_TODOS_USUARIOS')")
    @GetMapping("/inactive")
    public ResponseEntity<PagedModel<EntityModel<UserResponseDTO>>> getAllUsersInactive(Pageable pageable) {
        Page<UserResponseDTO> usersPage = userService.findAllInactive(pageable);
        PagedModel<EntityModel<UserResponseDTO>> model = pagedResourcesAssembler.toModel(usersPage, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns a specific user by ID if authorized."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User not authorized to access this resource",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('VER_USUARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();

        boolean isAdmin = credential.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !userId.equals(id)) {
            throw new OwnershipException("You do not have permission to access this resource.");
        }

        UserResponseDTO user = userService.findById(id);
        return ResponseEntity.ok(assembler.toModel(user));
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user account."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDTO>> createUser(@RequestBody @Valid UserCreateDTO user) {
        UserResponseDTO responseDTO = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(responseDTO));
    }

    @Operation(
            summary = "Update user by ID",
            description = "Updates the details of a user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('MODIFICAR_USUARIO_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO updatedUserDTO) {
        UserResponseDTO response = userService.update(id, updatedUserDTO);
        return ResponseEntity.ok(assembler.toModel(response));
    }

    @Operation(
            summary = "Update own account",
            description = "Allows the authenticated user to update their own profile information, such as username, email, password, DNI, and preferences. " +
                    "Only the fields included in the request will be updated (partial update)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('MODIFICAR_USUARIO')")
    @PutMapping("/me/update")
    public ResponseEntity<EntityModel<UserResponseDTO>> updateAccount(
            Authentication authentication,
            @RequestBody @Valid UserUpdateDTO updatedUserDTO) {
        String username = authentication.getName();
        UserResponseDTO response = userService.update(username, updatedUserDTO);
        return ResponseEntity.ok(assembler.toModel(response));
    }

    @Operation(
            summary = "Delete own account",
            description = "Deletes the authenticated user's account."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('ELIMINAR_USUARIO')")
    @DeleteMapping("/me/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteAccount(username);
        return ResponseEntity.ok("Account deleted successfully.");
    }

    @Operation(
            summary = "Delete a user account by ID (admin)",
            description = "Allows an administrator to delete any user account by its ID. This action performs a logical delete (sets the account as inactive)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User account deleted successfully",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – the user does not have the required authority",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('ELIMINAR_USUARIO_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAccountAdmin(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("Account deleted successfully.");
    }

    @Operation(
            summary = "Restore account",
            description = "Allows restoring an account if it was previously deleted (soft-deleted)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account restored successfully",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – user is not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or account not restorable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('RESTAURAR_USUARIO')")
    @PutMapping("/restore/{id}")
    public ResponseEntity<String> restoreAccount(@PathVariable Long id) {
        userService.restore(id);
        return ResponseEntity.ok("Account restored successfully.");
    }

    @Operation(
            summary = "Get own profile",
            description = "Retrieves the authenticated user's profile."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/me")
    public ResponseEntity<EntityModel<UserResponseDTO>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponseDTO profile = userService.getProfileByUsername(username);
        return ResponseEntity.ok(assembler.toModel(profile));
    }

    //solo para hateoas
    @Operation(summary = "Get user by ID (public)", description = "Retrieves a user's public information by their ID. Currently not implemented.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/public/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserByIdPublic(@PathVariable Long id) {
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Assign role to user",
            description = "Assigns a specific role to a user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role assigned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)) // Usamos el DTO de respuesta, no la entidad
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – User does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('ASIGNAR_ROLES')")
    @PutMapping("/roles/{id}")
    public ResponseEntity<String> assignRole(@PathVariable Long id){
        String mensaje = userService.assignRole(id);

        return ResponseEntity.ok(mensaje);
    }
}
