package com.example.demo.DTO.Trip;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripUpdateDTO {

    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double estimatedBudget;
    private Integer passengers;
    private Boolean active;

}
