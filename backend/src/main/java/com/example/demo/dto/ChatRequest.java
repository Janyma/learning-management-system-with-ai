package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest{
    @NotBlank(message = "Message must not be empty")
    private String message;

    private Long chatSessionId;

    public ChatRequest() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(Long chatSessionId) {
        this.chatSessionId = chatSessionId;
    }
}