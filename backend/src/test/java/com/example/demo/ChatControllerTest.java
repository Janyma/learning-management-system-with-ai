package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.example.demo.controller.ChatController;
import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.service.ChatService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChatControllerTest {

    private ChatController chatController;
    private String lastReceivedMessage;

    @BeforeEach
    void setUp() {
        // Manueller Stub statt Mockito – kein ByteBuddy nötig
        ChatService stubService = new ChatService() {
            @Override
            public String getChatResponse(String userMessage) {
                lastReceivedMessage = userMessage;
                return "AI says: " + userMessage;
            }
        };
        chatController = new ChatController(stubService);
    }

    @Test
    void shouldReturnReplyWhenValidMessage() {
        ChatRequest request = new ChatRequest();
        request.setMessage("Hello");

        ResponseEntity<ChatResponse> response = chatController.chat(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("AI says: Hello", response.getBody().getReply());
        assertEquals("Hello", lastReceivedMessage);
    }

    @Test
    void shouldPassMessageToChatService() {
        ChatRequest request = new ChatRequest();
        request.setMessage("Test message");

        ResponseEntity<ChatResponse> response = chatController.chat(request);

        assertEquals("Test message", lastReceivedMessage);
        assertEquals("AI says: Test message", response.getBody().getReply());
    }

    @Test
    void shouldReturnOkStatus() {
        ChatRequest request = new ChatRequest();
        request.setMessage("any");

        ResponseEntity<ChatResponse> response = chatController.chat(request);

        assertEquals(200, response.getStatusCode().value());
    }
}
