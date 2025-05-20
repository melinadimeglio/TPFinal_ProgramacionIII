package com.example.demo.DTO.Activity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ActivityUpdateDTO {

    private Double price;
    private Boolean availability;
    private String description;
    private String category;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}
