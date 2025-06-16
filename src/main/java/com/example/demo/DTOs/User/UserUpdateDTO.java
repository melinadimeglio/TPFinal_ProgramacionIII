package com.example.demo.DTOs.User;

import com.example.demo.DTOs.Trip.Response.TripResumeDTO;
import com.example.demo.enums.UserPreferences;
import com.example.demo.security.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class UserUpdateDTO {

    @Schema(description = "Nombre de usuario", example = "melinaD")
    @NotBlank(message = "El nombre de usuario no debe estar vacio.")
    @Column(unique = true)
    @Size(min = 5, max = 20, message = "El username debe tener entre 5 y 20 caracteres.")
    private String username;

    @Schema(description = "Email del usuario", example = "melina@example.com")
    @Email(message = "El email debe tener un formato válido.")
    @NotBlank(message = "El email no debe estar vacio.")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "Progra3_2025")
    @NotBlank(message = "La contraseña no debe estar vacia.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*!()_+=\\-]).{8,20}$",
            message = "La contraseña debe tener entre 8 y 20 caracteres, incluyendo una mayúscula, una minúscula, un número y un carácter especial."
    )
    private String password;

    @Schema(description = "DNI del usuario", example = "40123456")
    @NotBlank(message = "El DNI no debe estar vacio.")
    @Pattern(regexp = "^[0-9]{7,8}$", message = "El DNI debe tener entre 7 y 8 dígitos")
    private String dni;

    @Schema(description = "Preferencias de viaje del usuario", example = "[\"BEACHES\", \"SPORT\", \"FOODS\"]")
    private Set<UserPreferences> preferencias;

    @Schema(description = "Lista de viajes asociados al usuario")
    private List<TripResumeDTO> destinos;

}
