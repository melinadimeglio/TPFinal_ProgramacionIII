package com.example.demo.DTO.Trip;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripResponseDTO {

    @NotNull
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double estimatedBudget;
    private int passengers;
    private boolean active;
    private List<Long> userIds;

}
