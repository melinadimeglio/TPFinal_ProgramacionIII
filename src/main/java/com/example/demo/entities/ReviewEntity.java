package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "activity_id"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityEntity activity;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titulo;

    @NotBlank
    @Size(min = 20, max = 500)
    @Column(nullable = false, length = 500)
    private String comentario;

    @Min(1)
    @Max(5)
    @Column(name = "rating_guia", nullable = false)
    private Integer ratingGuia;

    @Min(1)
    @Max(5)
    @Column(name = "rating_puntualidad", nullable = false)
    private Integer ratingPuntualidad;

    @Min(1)
    @Max(5)
    @Column(name = "rating_precio", nullable = false)
    private Integer ratingPrecio;

    @Min(1)
    @Max(5)
    @Column(name = "rating_seguridad", nullable = false)
    private Integer ratingSeguridad;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
