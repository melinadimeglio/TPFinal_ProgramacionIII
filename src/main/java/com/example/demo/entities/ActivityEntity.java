package com.example.demo.entities;

import com.example.demo.enums.ActivityCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "Activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "itinerary")
@Builder
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;

    private String name;

    private String description;

    @Column(nullable = false)
    private boolean available = true;

    @Enumerated(EnumType.STRING)
    private ActivityCategory category;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id")
    private ItineraryEntity itinerary;

    @ManyToMany
    @JoinTable(
            name = "activity_user",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> users;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    private Integer available_quantity;
}