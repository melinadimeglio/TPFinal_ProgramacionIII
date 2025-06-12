package com.example.demo.DTOs.Activity.Response;

import com.example.demo.enums.ActivityCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponseDTO {

    private Long id;
    private Double price;
    private boolean available;
    private String name;
    private String description;
    private ActivityCategory category;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer available_quantity;
}