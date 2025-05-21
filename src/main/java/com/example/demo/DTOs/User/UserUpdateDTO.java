package com.example.demo.DTOs.User;

import com.example.demo.DTOs.Trip.TripResumeDTO;
import com.example.demo.enums.UserCategory;
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
    private UserCategory category;
    private Set<String> preferencias;
    private Boolean active;
    private List<TripResumeDTO> destinos;

}
