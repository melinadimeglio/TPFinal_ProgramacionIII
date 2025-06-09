package com.example.demo.DTOs.Reservation.Response;

import com.example.demo.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {

    private Long id;
    private Long userId;
    private Long activityId;
    private boolean paid;
    private Double amount;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
}
