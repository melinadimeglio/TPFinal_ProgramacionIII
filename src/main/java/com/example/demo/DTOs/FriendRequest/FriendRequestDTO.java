package com.example.demo.DTOs.FriendRequest;
import com.example.demo.enums.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestDTO {

    @Schema(description = "ID de la solicitud ", example = "12")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "melinaD")
    private String senderUsername;

    @Schema(description = "Estado de la solicitud de amistad", example = "ACCEPTED")
    private RequestStatus friendRequestStatus;

}
