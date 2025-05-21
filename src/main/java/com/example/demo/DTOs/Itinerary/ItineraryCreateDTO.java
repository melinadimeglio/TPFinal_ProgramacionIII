package com.example.demo.DTOs.Itinerary;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryCreateDTO {

    @NotNull(message = "La fecha del itinerario es obligatoria.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate itineraryDate;
    private String notes;
    private Long userId;
    private Long tripId;

}
