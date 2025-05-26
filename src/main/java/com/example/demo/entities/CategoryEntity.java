package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
