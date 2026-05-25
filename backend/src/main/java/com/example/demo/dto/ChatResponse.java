package com.example.demo.dto;

public class ChatResponse {

    private String reply;
    private Long chatSessionId;

    public ChatResponse(String reply, Long chatSessionId) {
        this.reply = reply;
        this.chatSessionId = chatSessionId;
    }

    public String getReply() {
        return reply;
    }

    public Long getChatSessionId() {
        return chatSessionId;
    }
}