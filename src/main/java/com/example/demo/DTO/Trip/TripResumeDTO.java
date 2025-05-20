package com.example.demo.DTO.Trip;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripResumeDTO {

    private Long id;
    private String destination;
    private boolean active;

}
