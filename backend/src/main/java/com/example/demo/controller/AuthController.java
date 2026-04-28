package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService=jwtService;
    }

        @RequestMapping("/register")
        public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userData, BindingResult bindingResult){
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errors);
                
            }
            
            try {
                User user = userService.registerUser(userData);
                return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "User created",
                    "userId", user.getId()
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
            }
        }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserLoginDto userData){
        User user = userService.findByUsername(userData.getUsername());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
            body(Map.of("error","Username or password is uncorrect."));
        }

        boolean matches = passwordEncoder.matches(userData.getPassword(), user.getPasswordHash());

        if(!matches){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Username or password is uncorrect-"));
        }
        String token =jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of(
            "message","Login successful",
            "token", token,
            "tokenType", "Bearer"));
        



        
    }

    
    

}