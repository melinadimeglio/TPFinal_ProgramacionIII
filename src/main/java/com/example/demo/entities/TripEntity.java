package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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

    @NotBlank(message = "El destino es obligatorio.")
    private String destination;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria.")
    private LocalDate endDate;

    @NotNull(message = "El presupuesto estimado es obligatorio.")
    @PositiveOrZero(message = "El presupuesto debe ser cero o positivo.")
    private Double estimatedBudget;

    @Min(value = 1, message = "Debe haber al menos un pasajero.")
    private int passengers;
    private boolean active;

    @NotNull(message = "Debe incluir al menos un usuario asociado.")
    @ManyToMany(mappedBy = "trips")
    private Set<UserEntity> users;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckListEntity> checklist;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<ItineraryEntity> itineraries;
}
