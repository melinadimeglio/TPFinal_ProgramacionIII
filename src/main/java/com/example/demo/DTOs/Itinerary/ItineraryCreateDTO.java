package com.example.demo.DTOs.Itinerary;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryCreateDTO {

    private LocalDate date;
    private LocalTime time;
    private String notes;
    private Long userId;
    private Long tripId;

}
