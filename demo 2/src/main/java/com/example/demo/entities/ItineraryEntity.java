package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Itinerary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItineraryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String notes;
}
