package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.controller.ChatController;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatSession;
import com.example.demo.model.User;
import com.example.demo.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnReplyWhenValidMessage() throws Exception {
        ChatService.ChatResult result = new ChatService.ChatResult("AI says: Hello", 1L);
        when(chatService.getChatResponse(eq("Hello"), isNull(), eq("testuser"))).thenReturn(result);

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("message", "Hello"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("AI says: Hello"))
                .andExpect(jsonPath("$.chatSessionId").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnReplyWithExistingSession() throws Exception {
        ChatService.ChatResult result = new ChatService.ChatResult("AI says: Hi", 5L);
        when(chatService.getChatResponse(eq("Hi"), eq(5L), eq("testuser"))).thenReturn(result);

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("message", "Hi", "chatSessionId", 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("AI says: Hi"))
                .andExpect(jsonPath("$.chatSessionId").value(5));
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenMessageIsBlank() throws Exception {
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("message", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenMessageIsMissing() throws Exception {
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("message", "Hello"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnSessionsForUser() throws Exception {
        ChatSession session = new ChatSession();
        session.setId(1L);
        session.setTitle("Test conversation");
        session.setCreatedAt(LocalDateTime.of(2026, 5, 25, 10, 0));

        when(chatService.getSessionsForUser("testuser")).thenReturn(List.of(session));

        mockMvc.perform(get("/api/chat/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test conversation"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnMessagesForSession() throws Exception {
        ChatMessage msg = new ChatMessage();
        msg.setId(1L);
        msg.setContent("Hello");
        msg.setRole("user");
        msg.setTimestamp(LocalDateTime.of(2026, 5, 25, 10, 0));

        when(chatService.getMessagesForSession(1L, "testuser")).thenReturn(List.of(msg));

        mockMvc.perform(get("/api/chat/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"))
                .andExpect(jsonPath("$[0].role").value("user"));
    }
}
