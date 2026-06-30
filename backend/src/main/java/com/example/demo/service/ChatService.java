package com.example.demo.service;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatSession;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatSessionRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatService {

    @Value("${ai.api.key}")
    private String apiKey;

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    public ChatSession getOrCreateSession(Long chatSessionId, String username, String firstMessage) {
        if (chatSessionId != null) {
            return chatSessionRepository.findById(chatSessionId)
                    .orElseThrow(() -> new RuntimeException("Chat session not found"));
        }

        User user = userRepository.findByUsername(username);
        String title = firstMessage.length() > 50 ? firstMessage.substring(0, 50) + "..." : firstMessage;

        ChatSession session = new ChatSession(title, user);
        return chatSessionRepository.save(session);
    }

    public ChatResult getChatResponse(String userMessage, Long chatSessionId, String username, String context) {
        ChatSession session = getOrCreateSession(chatSessionId, username, userMessage);

        // Save user message
        ChatMessage userMsg = new ChatMessage(userMessage, "user", session);
        chatMessageRepository.save(userMsg);

        // Get AI response, grounded in the on-screen content when provided
        String prompt = (context == null || context.isBlank())
                ? userMessage
                : "You are an assistant helping a student understand the course content shown below. " +
                  "Answer the student's question using this content as context.\n\n" +
                  "CONTENT:\n" + context + "\n\n" +
                  "QUESTION:\n" + userMessage;
        String aiReply = callAi(prompt);

        // Save AI response
        ChatMessage aiMsg = new ChatMessage(aiReply, "ai", session);
        chatMessageRepository.save(aiMsg);

        return new ChatResult(aiReply, session.getId());
    }

    private String callAi(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent";

        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": "%s" }
                      ]
                    }
                  ]
                }
                """.formatted(userMessage.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            String response = restTemplate.postForObject(url, request, String.class);

            // Parse the response to extract the text
            int textStart = response.indexOf("\"text\": \"") + 9;
            if (textStart < 9) {
                textStart = response.indexOf("\"text\":\"") + 8;
            }
            int textEnd = response.indexOf("\"", textStart);
            while (textEnd > 0 && response.charAt(textEnd - 1) == '\\') {
                textEnd = response.indexOf("\"", textEnd + 1);
            }

            if (textStart > 8 && textEnd > textStart) {
                return response.substring(textStart, textEnd)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
            }

            return response;
        } catch (Exception e) {
            return "Fehler bei der KI-Anfrage: " + e.getMessage();
        }
    }

    public java.util.List<ChatSession> getSessionsForUser(String username) {
        User user = userRepository.findByUsername(username);
        return chatSessionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public java.util.List<ChatMessage> getMessagesForSession(Long sessionId, String username) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        User user = userRepository.findByUsername(username);
        if (!session.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    public static class ChatResult {
        private final String reply;
        private final Long sessionId;

        public ChatResult(String reply, Long sessionId) {
            this.reply = reply;
            this.sessionId = sessionId;
        }

        public String getReply() {
            return reply;
        }

        public Long getSessionId() {
            return sessionId;
        }
    }
}