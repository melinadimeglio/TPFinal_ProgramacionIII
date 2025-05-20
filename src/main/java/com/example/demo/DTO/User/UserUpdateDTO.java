package com.example.demo.DTO.User;

import com.example.demo.DTO.Trip.TripResumeDTO;
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
    private String email;
    private String password;  // opcional para cambio de contraseña
    private String dni;
    private String category;
    private Set<String> preferencias;
    private Boolean active;
    private List<TripResumeDTO> destinos;

}
