package com.example.demo.DTOs.User.Response;

import com.example.demo.DTOs.Trip.Response.TripResumeDTO;
import com.example.demo.enums.UserPreferences;
import com.example.demo.security.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"active", "role"})
@Builder

public class UserResponseDTO {

    @Schema(description = "ID del usuario", example = "12")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "melinaD")
    private String username;

    @Schema(description = "DNI del usuario", example = "40123456")
    private String dni;

    @Schema(description = "Fecha de registro del usuario", example = "2024-03-15T10:45:00")
    private LocalDateTime fechaRegistro;

    @Schema(description = "Preferencias de viaje del usuario", example = "[\"BEACHES\", \"SPORT\", \"FOODS\"]")
    private Set<UserPreferences> preferencias;

    @Schema(description = "Indica si el usuario está activo", example = "true")
    private boolean active;

    @Schema(description = "Lista de viajes en los que participa el usuario")
    private List<TripResumeDTO> destinos;

}
