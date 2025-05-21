package com.example.demo.DTOs.Activity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ActivityResumeDTO {
    private String description;
}
