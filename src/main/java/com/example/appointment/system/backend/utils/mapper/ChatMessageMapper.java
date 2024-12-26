package com.example.appointment.system.backend.utils.mapper;

import com.example.appointment.system.backend.model.chat.ChatMessage;
import com.example.appointment.system.backend.dto.chat.ChatMessageDTO;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ChatMessageMapper {

    // Преобразование из модели ChatMessage в DTO
    public static ChatMessageDTO toDto(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setChatId(chatMessage.getChatId());
        chatMessageDTO.setSenderId(chatMessage.getSenderId());
        chatMessageDTO.setRecipientId(chatMessage.getRecipientId());
        chatMessageDTO.setSenderName(chatMessage.getSenderName());
        chatMessageDTO.setRecipientName(chatMessage.getRecipientName());
        chatMessageDTO.setContent(chatMessage.getContent());
        chatMessageDTO.setTimestamp(chatMessage.getTimestamp().getTime()); // Преобразование Date в long
        chatMessageDTO.setStatus(chatMessage.getStatus());

        return chatMessageDTO;
    }

    // Преобразование из DTO в модель ChatMessage
    public static ChatMessage toEntity(ChatMessageDTO chatMessageDTO) {
        if (chatMessageDTO == null) {
            return null;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(chatMessageDTO.getId());
        chatMessage.setChatId(chatMessageDTO.getChatId());
        chatMessage.setSenderId(chatMessageDTO.getSenderId());
        chatMessage.setRecipientId(chatMessageDTO.getRecipientId());
        chatMessage.setSenderName(chatMessageDTO.getSenderName());
        chatMessage.setRecipientName(chatMessageDTO.getRecipientName());
        chatMessage.setContent(chatMessageDTO.getContent());
        chatMessage.setTimestamp(new Date(chatMessageDTO.getTimestamp()));  // Преобразование long в Date
        chatMessage.setStatus(chatMessageDTO.getStatus());

        return chatMessage;
    }
}
