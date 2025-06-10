package com.example.demo.DTOs.Itinerary;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryUpdateDTO {

    @FutureOrPresent(message = "La fecha debe ser el día de hoy o posterior.")
    private LocalDate itineraryDate;

    private String notes;

}