package com.example.demo.entities;

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
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;
    private boolean availability;
    private String description;
    private String category; //enum
    private Date date;
    private Time time;
}
