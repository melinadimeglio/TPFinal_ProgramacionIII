package com.example.demo.DTOs;

import com.example.demo.entities.CategoryEntity;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class RecommendationDTO {

    private String name;
    private Set<CategoryEntity> categories;
    private Double lat;
    private Double lon;

}
