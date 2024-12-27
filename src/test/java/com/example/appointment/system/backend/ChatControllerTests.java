package com.example.appointment.system.backend;

import com.example.appointment.system.backend.controller.ChatController;
import com.example.appointment.system.backend.dto.chat.ChatMessageDTO;
import com.example.appointment.system.backend.model.chat.ChatMessage;
import com.example.appointment.system.backend.model.chat.ChatNotification;
import com.example.appointment.system.backend.service.ChatMessageService;
import com.example.appointment.system.backend.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ChatControllerTests {

    @InjectMocks
    private ChatController chatController;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private ChatRoomService chatRoomService;

    private UUID senderId;
    private UUID recipientId;
    private ChatMessage chatMessage;
    private ChatMessageDTO chatMessageDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        senderId = UUID.randomUUID();
        recipientId = UUID.randomUUID();
        chatMessage = new ChatMessage();
        chatMessage.setSenderId(senderId);
        chatMessage.setRecipientId(recipientId);
        chatMessage.setContent("Test message");

        // Создаем объект ChatMessageDTO с использованием сеттеров
        chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setId(UUID.randomUUID());
        chatMessageDTO.setSenderId(senderId);
        chatMessageDTO.setSenderName("Sender");
        chatMessageDTO.setContent("Test message");
    }

    @Test
    void testProcessMessage_Success() {
        // Создаем мок для метода chatRoomService.getChatId, чтобы вернуть id чата
        Optional<String> chatId = Optional.of("chat-id");
        when(chatRoomService.getChatId(senderId, recipientId, true)).thenReturn(chatId);

        // Мокаем сохранение сообщения
        when(chatMessageService.save(chatMessage)).thenReturn(chatMessageDTO);

        // Вызов метода processMessage
        chatController.processMessage(chatMessage);

        // Проверка, что метод convertAndSendToUser был вызван
        verify(messagingTemplate, times(1)).convertAndSendToUser(
                String.valueOf(recipientId), "/queue/messages",
                new ChatNotification(chatMessageDTO.getId(), chatMessageDTO.getSenderId(), chatMessageDTO.getSenderName())
        );

        // Проверка, что сообщение было сохранено
        verify(chatMessageService, times(1)).save(chatMessage);
    }

    @Test
    void testCountNewMessages() {
        long newMessagesCount = 5;
        // Мокаем метод countNewMessages
        when(chatMessageService.countNewMessages(senderId, recipientId)).thenReturn(newMessagesCount);

        // Вызов метода countNewMessages
        ResponseEntity<Long> response = chatController.countNewMessages(senderId, recipientId);

        // Проверка статуса и возвращаемого значения
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newMessagesCount, response.getBody());
    }

    @Test
    void testFindChatMessages() {
        // Создаем список сообщений
        ChatMessageDTO message1 = new ChatMessageDTO();
        message1.setId(UUID.randomUUID());
        message1.setSenderId(senderId);
        message1.setSenderName("Sender1");
        message1.setContent("Message 1");

        ChatMessageDTO message2 = new ChatMessageDTO();
        message2.setId(UUID.randomUUID());
        message2.setSenderId(senderId);
        message2.setSenderName("Sender2");
        message2.setContent("Message 2");

        List<ChatMessageDTO> messages = List.of(message1, message2);

        // Мокаем метод findChatMessages, чтобы он возвращал список сообщений
        when(chatMessageService.findChatMessages(senderId, recipientId)).thenReturn(messages);

        // Вызов метода findChatMessages
        ResponseEntity<?> response = chatController.findChatMessages(senderId, recipientId);

        // Проверка статуса и содержимого ответа
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages, response.getBody());
    }

    @Test
    void testFindMessage() {
        UUID messageId = UUID.randomUUID();
        // Мокаем метод findById
        when(chatMessageService.findById(messageId)).thenReturn(chatMessageDTO);

        // Вызов метода findMessage
        ResponseEntity<?> response = chatController.findMessage(messageId);

        // Проверка статуса и содержимого ответа
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(chatMessageDTO, response.getBody());
    }
}
