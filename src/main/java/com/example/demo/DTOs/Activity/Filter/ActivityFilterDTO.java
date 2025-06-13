package com.example.demo.DTOs.Activity.Filter;

import com.example.demo.enums.ActivityCategory;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ActivityFilterDTO {
    private ActivityCategory category;
    private LocalDate startDate;
    private LocalDate endDate;
}
