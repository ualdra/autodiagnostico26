package es.ual.dra.autodiagnostico.dto;

import java.time.LocalDateTime;

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
public class ChatMessageResponseDTO {

    private Long id;
    private String roomType;
    private Long participantId;
    private String senderRole;
    private String commentText;
    private Integer wordCount;
    private boolean readByUser;
    private LocalDateTime createdAt;
}
