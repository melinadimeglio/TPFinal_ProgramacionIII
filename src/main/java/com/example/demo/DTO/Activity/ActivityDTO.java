package com.example.demo.DTO.Activity;

import com.example.demo.enums.ActivityCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ActivityDTO {
    private Double price;
    private boolean availability;
    private String description;
    private String category;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long itineraryId;
    private Long userId;
}
