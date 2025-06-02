package com.example.demo.DTOs.Itinerary.Response;

import com.example.demo.DTOs.Activity.Response.ActivityResumeDTO;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryResponseDTO {

    private Long id;
    private LocalDate itineraryDate;
    private String notes;
    private List<ActivityResumeDTO> activities;
    private Long userId;
    private Long tripId;
}
