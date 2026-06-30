package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest{
    @NotBlank(message = "Message must not be empty")
    private String message;

    private Long chatSessionId;

    private String context;

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

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}