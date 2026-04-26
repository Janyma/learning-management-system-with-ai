package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Value("${ai.api.key}")
    private String apiKey;

    public String getChatResponse(String userMessage) {
                System.out.println("API KEY: " + apiKey);


        return "AI says: " + userMessage;
    }
}