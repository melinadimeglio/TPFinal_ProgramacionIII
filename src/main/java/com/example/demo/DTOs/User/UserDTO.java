package com.example.demo.DTOs.User;

import com.example.demo.DTOs.Trip.TripResumeDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String dni;
    private LocalDateTime fechaRegistro;
    private String category;
    private Set<String> preferencias;
    private boolean active;
    private List<TripResumeDTO> destinos; // ver si devolver en vez de id, una lista con los destinos

}
