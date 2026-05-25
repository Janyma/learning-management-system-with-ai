package com.example.demo.service;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatSession;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatSessionRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public ChatResult getChatResponse(String userMessage, Long chatSessionId, String username) {
        ChatSession session = getOrCreateSession(chatSessionId, username, userMessage);

        // Save user message
        ChatMessage userMsg = new ChatMessage(userMessage, "user", session);
        chatMessageRepository.save(userMsg);

        // Get AI response
        String aiReply = callAi(userMessage);

        // Save AI response
        ChatMessage aiMsg = new ChatMessage(aiReply, "ai", session);
        chatMessageRepository.save(aiMsg);

        return new ChatResult(aiReply, session.getId());
    }

    private String callAi(String userMessage) {
        System.out.println("API KEY: " + apiKey);
        return "AI says: " + userMessage;
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