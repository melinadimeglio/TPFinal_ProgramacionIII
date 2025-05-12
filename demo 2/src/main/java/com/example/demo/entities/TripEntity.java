package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

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
    private Date starDate;
    private Date endDate;
    private Double estimatedBudget;
    private int passengers;
    private boolean active;

    private List<UserEntity> users;
}
