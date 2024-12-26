package com.example.appointment.system.backend.model.chat;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id")
    private UUID id;  // UUID вместо String

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;  // UUID вместо String

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;  // UUID вместо String

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status;
}
