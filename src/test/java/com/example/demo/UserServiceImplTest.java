package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock 
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;


	@Test
	void shouldRegisterUserSuccessfully() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("test123");
        dto.setPassword("password");
        when(userRepository.findByUsername("test123")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.setUsername("test123");
        savedUser.setPasswordHash("hashedPassword");

        when(userRepository.save(Mockito.<User>any())).thenReturn(savedUser);

        User result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("test123", result.getUsername());
        assertEquals("hashedPassword", result.getPasswordHash());

        verify(userRepository).findByUsername("test123");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));



	}

    @Test
	void shouldThrowErrorIfUserExists() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("test123");
        dto.setPassword("password");
        when(userRepository.findByUsername("test123")).thenReturn(new User());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(dto)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).findByUsername("test123");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        



	}

    @Test
	void shouldFindByName() {
        User user = new User();
        user.setUsername("username123");
        when(userRepository.findByUsername("username123")).thenReturn(user);

        User result = userRepository.findByUsername("username123");
        assertNotNull(result);
        assertEquals("username123", result.getUsername());




	}

    @Test
	void shouldNotFindByName() {
        User username = new User();
        username.setUsername("username123");
        when(userRepository.findByUsername("username123")).thenReturn(null);
        User result = userRepository.findByUsername("username123");


        assertNull(result);


	}


    

}
