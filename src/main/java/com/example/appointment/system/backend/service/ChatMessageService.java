package com.example.appointment.system.backend.service;

import com.example.appointment.system.backend.dto.chat.ChatMessageDTO;
import com.example.appointment.system.backend.exception.ResourceNotFoundException;
import com.example.appointment.system.backend.model.chat.ChatMessage;
import com.example.appointment.system.backend.model.chat.MessageStatus;
import com.example.appointment.system.backend.repository.ChatMessageRepository;
import com.example.appointment.system.backend.utils.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageDTO save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        ChatMessage savedMessage = repository.save(chatMessage);
        return chatMessageMapper.toDto(savedMessage);
    }

    public long countNewMessages(UUID senderId, UUID recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessageDTO> findChatMessages(UUID senderId, UUID recipientId) {
        var chatId = chatRoomService.getChatId(senderId, recipientId, false);
        var messages = chatId.map(repository::findByChatId).orElse(List.of());
        if (!messages.isEmpty()) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }
        return Collections.singletonList(chatMessageMapper.toDto((ChatMessage) messages));
    }

    public ChatMessageDTO findById(UUID id) {
        return repository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return chatMessageMapper.toDto(repository.save(chatMessage));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Message not found (" + id + ")"));
    }

    public void updateStatuses(UUID senderId, UUID recipientId, MessageStatus status) {
        repository.updateStatuses(senderId, recipientId, status);
    }
}
