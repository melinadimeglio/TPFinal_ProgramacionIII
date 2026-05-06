package com.example.demo.DTOs.Notification.Request;

import com.example.demo.enums.NotificationCategory;
import com.example.demo.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateDTO {
    @Schema(description = "ID del usuario destinatario de la notificación", example = "42")
    @NotNull(message = "El usuario destinatario es obligatorio.")
    private Long userId;

    @Schema(description = "Tipo de notificación", example = "PAYMENT_CONFIRMED")
    @NotNull(message = "El tipo de notificación es obligatorio.")
    private NotificationType type;

    @Schema(description = "Categoría de la notificación (define en qué tab aparece)", example = "PAYMENTS")
    @NotNull(message = "La categoría es obligatoria.")
    private NotificationCategory category;

    @Schema(description = "Título corto de la notificación", example = "Pago confirmado")
    @NotBlank(message = "El título es obligatorio.")
    @Size(max = 120, message = "El título no puede superar los 120 caracteres.")
    private String title;

    @Schema(description = "Cuerpo del mensaje de la notificación", example = "Tu pago por \"Trekking Cerro Tronador\" fue procesado exitosamente.")
    @NotBlank(message = "El cuerpo del mensaje es obligatorio.")
    @Size(max = 300, message = "El cuerpo no puede superar los 300 caracteres.")
    private String body;

    @Schema(description = "ID de la entidad relacionada (reserva, gasto, viaje, etc.)", example = "15")
    private Long relatedEntityId;

    @Schema(description = "Tipo de la entidad relacionada", example = "RESERVATION",
            allowableValues = {"RESERVATION", "EXPENSE", "TRIP", "USER"})
    private String relatedEntityType;
}
