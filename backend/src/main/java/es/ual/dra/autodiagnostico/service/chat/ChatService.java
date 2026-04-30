package es.ual.dra.autodiagnostico.service.chat;

import java.util.List;

import es.ual.dra.autodiagnostico.dto.ChatJoinResponseDTO;
import es.ual.dra.autodiagnostico.dto.ChatMessageRequestDTO;
import es.ual.dra.autodiagnostico.dto.ChatMessageResponseDTO;

public interface ChatService {

    ChatJoinResponseDTO joinRoom(String roomType, Long participantId);

    ChatJoinResponseDTO leaveRoom(String roomType, Long participantId);

    List<ChatMessageResponseDTO> listMessages(String roomType, Integer limit, Long afterId);

    ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO dto);

    long unreadCount(String roomType);

    int markReadByUser(String roomType);

    boolean isUserOnline(String roomType, Long participantId);
}
