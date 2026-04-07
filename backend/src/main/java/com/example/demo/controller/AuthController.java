package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

        @RequestMapping("/register")
        public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userData){
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

    @RequestMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginDto userData){
        User user = userService.findByUsername(userData.getUsername());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is uncorrect.");
        }

        boolean matches = passwordEncoder.matches(userData.getPassword(), user.getPasswordHash());

        if(!matches){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is uncorrect-");
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Correct password");
        



        
    }

    
    

}