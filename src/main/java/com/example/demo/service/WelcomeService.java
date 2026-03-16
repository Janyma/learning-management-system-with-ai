package com.example.demo.service;
import org.springframework.stereotype.Service;


@Service
public class WelcomeService {

    public String getWelcomeMessage() {
        return "Welcome to the AI Learning Management System!";
    }
}