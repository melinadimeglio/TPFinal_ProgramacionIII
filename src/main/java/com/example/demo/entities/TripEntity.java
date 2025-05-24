package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Trip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destination;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double estimatedBudget;

    private int companions;
    private boolean active = true;

    @ManyToMany(mappedBy = "trips")
    private Set<UserEntity> users;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckListEntity> checklist;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<ItineraryEntity> itineraries;
}
