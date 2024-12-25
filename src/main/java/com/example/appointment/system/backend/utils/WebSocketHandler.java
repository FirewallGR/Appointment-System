package com.example.appointment.system.backend.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String[] pathParts = session.getUri().getPath().split("/");
        if (pathParts.length < 4) {
            session.close(CloseStatus.BAD_DATA);
            System.out.println("Некорректный URL для WebSocket соединения.");
            return;
        }

        String role = pathParts[2];
        String id = pathParts[3];

        sessions.put(id, session);

        System.out.println("Подключен " + role + ": " + id);
        for (String key : sessions.keySet()) {
            WebSocketSession value = sessions.get(key);
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Декодируем сообщение из JSON
        String payload = message.getPayload();
        Map<String, String> messageData = parseMessage(payload);
        System.out.println("Session2 " + session);
        if (!messageData.containsKey("targetId") || !messageData.containsKey("message")) {
            System.out.println("Некорректный формат сообщения: " + payload);
            return;
        }

        String targetId = messageData.get("targetId");
        String messageContent = messageData.get("message");

        // Находим сессию получателя
        WebSocketSession targetSession = sessions.get(targetId);
        System.out.println("targetSession + "+ targetSession + " isOpen " + targetSession.isOpen());
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(new TextMessage(messageContent));
            System.out.println("Сообщение отправлено: " + targetId + " -> " + messageContent);
        } else {
            System.out.println("Получатель не найден или соединение закрыто: " + targetId);
        }
        System.out.println("Session3 " + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.values().remove(session);
        System.out.println("Сессия закрыта: " + session.getId());
    }

    private Map<String, String> parseMessage(String payload) {
        Map<String, String> result = new ConcurrentHashMap<>();
        try {
            String[] pairs = payload.replace("{", "").replace("}", "").replace("\"", "").split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                result.put(keyValue[0].trim(), keyValue[1].trim());
            }
        } catch (Exception e) {
            System.out.println("Ошибка разбора сообщения: " + payload);
        }
        return result;
    }
}
