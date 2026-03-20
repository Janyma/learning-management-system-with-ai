package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRegistrationDto userData) {


        if (userRepository.findByUsername(userData.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
            }
        
        String hashedPassword = passwordEncoder.encode(userData.getPassword());
        User user = new User();
        user.setUsername(userData.getUsername());
        user.setPasswordHash(hashedPassword);
        return userRepository.save(user);
    
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}