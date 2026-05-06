package com.example.demo.DTOs.Notification;

import com.example.demo.DTOs.Notification.Response.NotificationResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Summary {
    @Schema(description = "Cantidad de notificaciones no leídas del usuario", example = "4")
    private long unreadCount;

    @Schema(description = "Lista de las últimas notificaciones del usuario (máximo 30)")
    private List<NotificationResponseDTO> notifications;
}
