package es.ual.dra.autodiagnostico.dto;

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
public class ChatJoinResponseDTO {

    private String roomType;
    private Long participantId;
    private Integer activeUsers;
    private Integer maxUsers;
    private boolean joined;
}
