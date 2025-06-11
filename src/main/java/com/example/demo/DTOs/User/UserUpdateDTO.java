package com.example.demo.DTOs.User;

import com.example.demo.DTOs.Trip.Response.TripResumeDTO;
import com.example.demo.enums.UserPreferences;
import com.example.demo.security.enums.Role;
import jakarta.validation.constraints.Email;
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

    private String username;

    @Email(message = "El email debe tener un formato válido.")
    private String email;
    private String password;  // opcional para cambio de contraseña
    private String dni;
    private Role role;
    private Set<UserPreferences> preferencias;
    private List<TripResumeDTO> destinos;

}
