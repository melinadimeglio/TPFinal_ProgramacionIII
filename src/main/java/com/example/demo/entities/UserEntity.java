package com.example.demo.entities;

import com.example.demo.enums.UserCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @Column(unique = true)
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Column(unique = true)
    private String dni;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    private UserCategory category;

    @ElementCollection
    private Set<String> preferencias;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private TripEntity trip;
}
