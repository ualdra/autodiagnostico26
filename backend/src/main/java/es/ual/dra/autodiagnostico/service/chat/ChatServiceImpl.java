package es.ual.dra.autodiagnostico.service.chat;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ual.dra.autodiagnostico.dto.ChatJoinResponseDTO;
import es.ual.dra.autodiagnostico.dto.ChatMessageRequestDTO;
import es.ual.dra.autodiagnostico.dto.ChatMessageResponseDTO;
import es.ual.dra.autodiagnostico.model.entitity.chat.ChatMessage;
import es.ual.dra.autodiagnostico.model.entitity.chat.ChatRoomPresence;
import es.ual.dra.autodiagnostico.model.entitity.chat.ChatRoomType;
import es.ual.dra.autodiagnostico.model.entitity.chat.ChatSenderRole;
import es.ual.dra.autodiagnostico.repository.chat.ChatMessageRepository;
import es.ual.dra.autodiagnostico.repository.chat.ChatRoomPresenceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private static final int MAX_USERS_PER_ROOM = 10;
    private static final int MAX_WORDS_PER_MESSAGE = 500;
    private static final int MAX_FETCH_LIMIT = 100;
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomPresenceRepository chatRoomPresenceRepository;

    @Override
    public ChatJoinResponseDTO joinRoom(String roomType, Long participantId) {
        ChatRoomType parsedRoom = ChatRoomType.from(roomType);
        validateParticipantId(participantId);

        ChatRoomPresence presence = chatRoomPresenceRepository
                .findByRoomTypeAndParticipantId(parsedRoom, participantId)
                .orElse(null);

        if (presence == null || !presence.isActive()) {
            long currentActive = chatRoomPresenceRepository.countByRoomTypeAndActiveIsTrue(parsedRoom);
            if (currentActive >= MAX_USERS_PER_ROOM) {
                throw new IllegalArgumentException("La sala esta llena. Maximo 10 personas por chat");
            }

            if (presence == null) {
                presence = ChatRoomPresence.builder()
                        .roomType(parsedRoom)
                        .participantId(participantId)
                        .active(true)
                        .build();
            } else {
                presence.setActive(true);
            }

            chatRoomPresenceRepository.save(presence);
        }

        int activeUsers = (int) chatRoomPresenceRepository.countByRoomTypeAndActiveIsTrue(parsedRoom);
        return ChatJoinResponseDTO.builder()
                .roomType(parsedRoom.name())
                .participantId(participantId)
                .activeUsers(activeUsers)
                .maxUsers(MAX_USERS_PER_ROOM)
                .joined(true)
                .build();
    }

    @Override
    public ChatJoinResponseDTO leaveRoom(String roomType, Long participantId) {
        ChatRoomType parsedRoom = ChatRoomType.from(roomType);
        validateParticipantId(participantId);

        chatRoomPresenceRepository.findByRoomTypeAndParticipantId(parsedRoom, participantId).ifPresent(presence -> {
            presence.setActive(false);
            chatRoomPresenceRepository.save(presence);
        });

        int activeUsers = (int) chatRoomPresenceRepository.countByRoomTypeAndActiveIsTrue(parsedRoom);
        return ChatJoinResponseDTO.builder()
                .roomType(parsedRoom.name())
                .participantId(participantId)
                .activeUsers(activeUsers)
                .maxUsers(MAX_USERS_PER_ROOM)
                .joined(false)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDTO> listMessages(String roomType, Integer limit, Long afterId) {
        ChatRoomType parsedRoom = ChatRoomType.from(roomType);
        int safeLimit = limit == null ? 50 : Math.max(1, Math.min(limit, MAX_FETCH_LIMIT));

        List<ChatMessage> result;
        if (afterId != null && afterId > 0) {
            result = chatMessageRepository.findTop100ByRoomTypeAndIdGreaterThanOrderByIdAsc(parsedRoom, afterId)
                    .stream()
                    .limit(safeLimit)
                    .toList();
        } else {
            List<ChatMessage> ordered = chatMessageRepository.findTop100ByRoomTypeOrderByCreatedAtDesc(parsedRoom)
                    .stream()
                    .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                    .toList();
            int start = Math.max(0, ordered.size() - safeLimit);
            result = ordered.subList(start, ordered.size());
        }

        return result.stream().map(this::toDTO).toList();
    }

    @Override
    public ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO dto) {
        ChatRoomType parsedRoom = ChatRoomType.from(dto.getRoomType());
        ChatSenderRole senderRole = ChatSenderRole.from(dto.getSenderRole());

        validateParticipantId(dto.getParticipantId());
        ensureParticipantInRoom(parsedRoom, dto.getParticipantId());

        String normalizedComment = normalizeComment(dto.getCommentText());
        int wordCount = countWords(normalizedComment);

        if (wordCount == 0) {
            throw new IllegalArgumentException("El comentario no puede estar vacio");
        }
        if (wordCount > MAX_WORDS_PER_MESSAGE) {
            throw new IllegalArgumentException("El comentario excede 500 palabras");
        }

        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .roomType(parsedRoom)
                        .participantId(dto.getParticipantId())
                        .senderRole(senderRole)
                        .commentText(normalizedComment)
                        .wordCount(wordCount)
                        .readByUser(senderRole == ChatSenderRole.USUARIO)
                        .build());

        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(String roomType) {
        ChatRoomType parsedRoom = ChatRoomType.from(roomType);
        return chatMessageRepository.countByRoomTypeAndSenderRoleAndReadByUserFalse(parsedRoom, ChatSenderRole.MECANICO);
    }

    @Override
    public int markReadByUser(String roomType) {
        ChatRoomType parsedRoom = ChatRoomType.from(roomType);
        return chatMessageRepository.markReadByUser(parsedRoom, ChatSenderRole.MECANICO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserOnline(String roomType, Long participantId) {
        ChatRoomType parsedRoom = ChatRoomType.from(roomType);
        validateParticipantId(participantId);
        return chatRoomPresenceRepository.findByRoomTypeAndParticipantId(parsedRoom, participantId)
                .map(ChatRoomPresence::isActive)
                .orElse(false);
    }

    private void ensureParticipantInRoom(ChatRoomType roomType, Long participantId) {
        ChatRoomPresence presence = chatRoomPresenceRepository
                .findByRoomTypeAndParticipantId(roomType, participantId)
                .orElseThrow(() -> new IllegalArgumentException("Debes unirte a la sala antes de enviar mensajes"));

        if (!presence.isActive()) {
            throw new IllegalArgumentException("Debes unirte a la sala antes de enviar mensajes");
        }
    }

    private void validateParticipantId(Long participantId) {
        if (participantId == null || participantId <= 0) {
            throw new IllegalArgumentException("El participante es obligatorio");
        }
    }

    private String normalizeComment(String value) {
        return value == null ? "" : value.trim();
    }

    private int countWords(String text) {
        String trimmed = text == null ? "" : text.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return WHITESPACE_PATTERN.split(trimmed).length;
    }

    private ChatMessageResponseDTO toDTO(ChatMessage message) {
        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .roomType(message.getRoomType().name())
                .participantId(message.getParticipantId())
                .senderRole(message.getSenderRole().name())
                .commentText(message.getCommentText())
                .wordCount(message.getWordCount())
                .readByUser(message.isReadByUser())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
