package com.example.demo.DTOs.Trip;

import com.example.demo.enums.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripInvitationDTO {

    @Schema(description = "ID de la solicitud ", example = "12")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "melinaD")
    private String senderUsername;

    @Schema(description = "ID del viaje ", example = "15")
    private Long tripId;

    @Schema(description = "Destino del viaje", example = "Brasil")
    private String destination;

    @Schema(description = "Estado de la solicitud de viaje", example = "ACCEPTED")
    private RequestStatus tripInvitationStatus;
}
