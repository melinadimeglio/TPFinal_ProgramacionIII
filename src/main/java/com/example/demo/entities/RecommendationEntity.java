package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "Recommendation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class RecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "recommendation_categories",
            joinColumns = @JoinColumn(name = "recommendation_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<CategoryEntity> categories;
    private Double lat;
    private Double lon;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private TripEntity trip;

}
