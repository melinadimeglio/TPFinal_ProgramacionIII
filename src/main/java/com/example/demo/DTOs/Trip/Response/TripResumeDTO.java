package com.example.demo.DTOs.Trip.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripResumeDTO {

    private Long id;
    private String name;
    private String destination;
    private boolean active;

}
