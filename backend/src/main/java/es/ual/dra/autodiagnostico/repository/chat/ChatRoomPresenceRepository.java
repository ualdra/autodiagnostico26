package es.ual.dra.autodiagnostico.repository.chat;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.ual.dra.autodiagnostico.model.entitity.chat.ChatRoomPresence;
import es.ual.dra.autodiagnostico.model.entitity.chat.ChatRoomType;

@Repository
public interface ChatRoomPresenceRepository extends JpaRepository<ChatRoomPresence, Long> {

    long countByRoomTypeAndActiveIsTrue(ChatRoomType roomType);

    Optional<ChatRoomPresence> findByRoomTypeAndParticipantId(ChatRoomType roomType, Long participantId);
}
