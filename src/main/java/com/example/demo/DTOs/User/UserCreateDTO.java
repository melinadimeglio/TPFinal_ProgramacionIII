package com.example.demo.DTOs.User;

import com.example.demo.DTOs.Trip.TripResumeDTO;
import com.example.demo.enums.UserCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class UserCreateDTO {

    @NotBlank(message = "El nombre de usuario no debe estar vacio.")
    private String username;

    @Email(message = "El email debe tener un formato válido.")
    @NotBlank(message = "El email no debe estar vacio.")
    private String email;

    @NotBlank(message = "La contraseña no debe estar vacia.")
    private String password;

    @NotBlank(message = "El DNI no debe estar vacio.")
    private String dni;

    private UserCategory category;

    private Set<String> preferencias;

}
