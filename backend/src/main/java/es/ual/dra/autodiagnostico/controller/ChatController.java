package es.ual.dra.autodiagnostico.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.ual.dra.autodiagnostico.dto.ChatJoinResponseDTO;
import es.ual.dra.autodiagnostico.dto.ChatMessageRequestDTO;
import es.ual.dra.autodiagnostico.dto.ChatMessageResponseDTO;
import es.ual.dra.autodiagnostico.service.chat.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{roomType}/join")
    public ResponseEntity<ChatJoinResponseDTO> joinRoom(
            @PathVariable String roomType,
            @RequestParam Long participantId
    ) {
        return ResponseEntity.ok(chatService.joinRoom(roomType, participantId));
    }

    @PostMapping("/{roomType}/leave")
    public ResponseEntity<ChatJoinResponseDTO> leaveRoom(
            @PathVariable String roomType,
            @RequestParam Long participantId
    ) {
        return ResponseEntity.ok(chatService.leaveRoom(roomType, participantId));
    }

    @GetMapping("/{roomType}/mensajes")
    public ResponseEntity<List<ChatMessageResponseDTO>> listMessages(
            @PathVariable String roomType,
            @RequestParam(defaultValue = "50") Integer limit,
            @RequestParam(required = false) Long afterId
    ) {
        return ResponseEntity.ok(chatService.listMessages(roomType, limit, afterId));
    }

    @PostMapping("/mensajes")
    public ResponseEntity<ChatMessageResponseDTO> sendMessage(@Valid @RequestBody ChatMessageRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(dto));
    }

    @GetMapping("/{roomType}/unread")
    public ResponseEntity<Long> unreadCount(@PathVariable String roomType) {
        return ResponseEntity.ok(chatService.unreadCount(roomType));
    }

    @PostMapping("/{roomType}/mark-read")
    public ResponseEntity<Integer> markReadByUser(@PathVariable String roomType) {
        return ResponseEntity.ok(chatService.markReadByUser(roomType));
    }

    @GetMapping("/{roomType}/presence")
    public ResponseEntity<Boolean> userPresence(
            @PathVariable String roomType,
            @RequestParam Long participantId
    ) {
        return ResponseEntity.ok(chatService.isUserOnline(roomType, participantId));
    }
}
