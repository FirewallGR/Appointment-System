package com.example.appointment.system.backend.repository;

import com.example.appointment.system.backend.model.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(UUID senderId, UUID recipientId);
}
