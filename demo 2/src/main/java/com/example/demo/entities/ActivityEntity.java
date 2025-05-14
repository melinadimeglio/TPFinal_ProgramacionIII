package com.example.demo.entities;

import com.example.demo.enums.ActivityCategory;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "Activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;
    private boolean availability;
    private String description;
    @Enumerated(EnumType.STRING)
    private ActivityCategory category;
    private Date date;
    private Time time;

    //comentario
}
