package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CheckList")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CheckListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String item;
    private boolean status;
}
