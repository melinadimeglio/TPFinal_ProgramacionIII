package com.example.demo.security.entities;

import com.example.demo.security.enums.Permit;
import jakarta.persistence.*;

@Entity
public class PermitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    Permit permit;
}
