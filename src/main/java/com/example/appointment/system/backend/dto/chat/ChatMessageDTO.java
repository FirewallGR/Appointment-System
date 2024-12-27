package com.example.appointment.system.backend.dto.chat;

import com.example.appointment.system.backend.model.chat.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private UUID id;
    private String chatId;
    private UUID senderId;  // UUID вместо String
    private UUID recipientId;  // UUID вместо String
    private String senderName;
    private String recipientName;
    private String content;
    private long timestamp;
    private MessageStatus status;
}

