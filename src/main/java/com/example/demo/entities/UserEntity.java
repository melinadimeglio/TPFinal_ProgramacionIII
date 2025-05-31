package com.example.demo.entities;

import com.example.demo.enums.UserCategory;
import com.example.demo.enums.UserPreferences;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "active = true")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
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
    private Role role;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<UserPreferences> preferencias;

    @Builder.Default
    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "user_trip",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "trip_id")
    )
    private Set<TripEntity> trips = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CredentialEntity credential;

}
