package com.example.demo.DTOs.User.Request;

import com.example.demo.enums.UserPreferences;
import com.example.demo.security.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class UserCreateDTO {

    @Schema(description = "Nombre de usuario", example = "melinaD")
    @NotBlank(message = "El nombre de usuario no debe estar vacio.")
    @Column(unique = true)
    private String username;

    @Schema(description = "Email del usuario", example = "melina@example.com")
    @Email(message = "El email debe tener un formato válido.")
    @NotBlank(message = "El email no debe estar vacio.")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "PasswordSegura123")
    @NotBlank(message = "La contraseña no debe estar vacia.")
    private String password;

    @Schema(description = "DNI del usuario", example = "40123456")
    @NotBlank(message = "El DNI no debe estar vacio.")
    private String dni;

    @Schema(description = "Preferencias de viaje del usuario", example = "[\"PLAYA\", \"AVENTURA\", \"CULTURA\"]")
    private Set<UserPreferences> preferencias;
}
