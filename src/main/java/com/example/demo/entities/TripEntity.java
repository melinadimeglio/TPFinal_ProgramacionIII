package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Trip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String destination;
    private LocalDate starDate;
    private LocalDate endDate;
    private Double estimatedBudget;
    private int passengers;
    private boolean active;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<UserEntity> users;
}
