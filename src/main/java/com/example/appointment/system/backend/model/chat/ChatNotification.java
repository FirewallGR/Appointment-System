package com.example.appointment.system.backend.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatNotification {
    private UUID id;
    private UUID senderId;
    private String senderName;
}