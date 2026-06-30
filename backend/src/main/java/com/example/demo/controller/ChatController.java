package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatSession;
import com.example.demo.service.ChatService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/chat")
public class ChatController{


    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        ChatService.ChatResult result = chatService.getChatResponse(
                request.getMessage(), request.getChatSessionId(), username, request.getContext());

        return ResponseEntity.ok(new ChatResponse(result.getReply(), result.getSessionId()));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> getSessions(Authentication authentication) {
        String username = authentication.getName();
        List<ChatSession> sessions = chatService.getSessionsForUser(username);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getSessionMessages(
            @PathVariable Long sessionId,
            Authentication authentication) {
        String username = authentication.getName();
        List<ChatMessage> messages = chatService.getMessagesForSession(sessionId, username);
        return ResponseEntity.ok(messages);
    }


}