package com.example.demo.DTOs.Itinerary;

import com.example.demo.DTOs.Activity.ActivityResponseDTO;
import com.example.demo.DTOs.Activity.ActivityResumeDTO;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryResponseDTO {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String notes;
    private List<ActivityResumeDTO> activities;
    private Long userId;
    private Long tripId;

}
