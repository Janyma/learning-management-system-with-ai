package com.example.demo.service;

import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.User;

public interface UserService {

    User registerUser(UserRegistrationDto userData);
    User findByUsername(String username);

}

