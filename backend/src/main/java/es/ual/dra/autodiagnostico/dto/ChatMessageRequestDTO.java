package es.ual.dra.autodiagnostico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDTO {

    @NotNull(message = "El participante es obligatorio")
    private Long participantId;

    @NotBlank(message = "La sala es obligatoria")
    private String roomType;

    @NotBlank(message = "El rol del remitente es obligatorio")
    private String senderRole;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 6000, message = "El comentario excede el tamano permitido")
    private String commentText;
}
