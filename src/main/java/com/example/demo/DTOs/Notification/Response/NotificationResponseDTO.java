package com.example.demo.DTOs.Notification.Response;

import com.example.demo.enums.NotificationCategory;
import com.example.demo.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    @Schema(description = "ID único de la notificación", example = "101")
    private Long id;

    @Schema(description = "Tipo de notificación", example = "PAYMENT_CONFIRMED")
    private NotificationType type;

    @Schema(description = "Categoría de la notificación", example = "PAYMENTS")
    private NotificationCategory category;

    @Schema(description = "Título corto de la notificación", example = "Pago confirmado")
    private String title;

    @Schema(description = "Cuerpo del mensaje", example = "Tu pago por \"Trekking Cerro Tronador\" fue procesado exitosamente.")
    private String body;

    @Schema(description = "ID de la entidad relacionada", example = "15")
    private Long relatedEntityId;

    @Schema(description = "Tipo de la entidad relacionada", example = "RESERVATION")
    private String relatedEntityType;

    @Schema(description = "Indica si la notificación fue leída", example = "false")
    private boolean read;

    @Schema(description = "Fecha y hora de creación de la notificación", example = "2025-06-20T09:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha y hora en que fue marcada como leída (null si no fue leída)", example = "2025-06-20T10:15:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readAt;
}
