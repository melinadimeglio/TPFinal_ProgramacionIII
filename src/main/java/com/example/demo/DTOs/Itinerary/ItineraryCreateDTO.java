package com.example.demo.DTOs.Itinerary;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryCreateDTO {

    @NotNull(message = "La fecha del itinerario es obligatoria.")
    private LocalDate date;
    private String notes;
    private Long userId;
    private Long tripId;

}
