package com.example.demo.DTOs.Filter;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripFilterDTO {
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
}
