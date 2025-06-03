package com.example.demo.DTOs.User.Response;

import com.example.demo.DTOs.Trip.Response.TripResumeDTO;
import com.example.demo.enums.UserPreferences;
import com.example.demo.security.enums.Role;
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

    private Long id;
    private String username;
    private String dni;
    private LocalDateTime fechaRegistro;
    private Set<UserPreferences> preferencias;
    private boolean active;
    private List<TripResumeDTO> destinos; // ver si devolver en vez de id, una lista con los destinos

}
