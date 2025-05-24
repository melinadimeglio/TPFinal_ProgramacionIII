package com.example.demo.DTOs.Trip;

import lombok.*;

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
