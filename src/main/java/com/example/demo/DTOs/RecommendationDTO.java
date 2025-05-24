package com.example.demo.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class RecommendationDTO {

    private String name;
    private String description;
    private Double lat;
    private Double lon;

}
