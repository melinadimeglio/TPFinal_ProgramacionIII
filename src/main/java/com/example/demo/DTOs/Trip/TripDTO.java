package com.example.demo.DTOs.Trip;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripDTO {

    private Long id;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double estimatedBudget;
    private int passengers;
    private boolean active;
    private List<Long> userIds;

}
