package com.example.appointment.system.backend.repository;

import com.example.appointment.system.backend.model.chat.ChatMessage;
import com.example.appointment.system.backend.model.chat.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    long countBySenderIdAndRecipientIdAndStatus(
            UUID senderId, UUID recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage cm SET cm.status = :status WHERE cm.senderId = :senderId AND cm.recipientId = :recipientId")
    void updateStatuses(UUID senderId, UUID recipientId, MessageStatus status);
}
