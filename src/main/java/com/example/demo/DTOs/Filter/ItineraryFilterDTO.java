package com.example.demo.DTOs.Filter;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryFilterDTO {
        private String dateFrom;
        private String dateTo;
 }