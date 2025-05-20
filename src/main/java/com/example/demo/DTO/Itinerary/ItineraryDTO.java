package com.example.demo.DTO.Itinerary;

import com.example.demo.DTO.Activity.ActivityDTO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryDTO {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String notes;
    private List<ActivityDTO> activities;
    private Long userId;

}
