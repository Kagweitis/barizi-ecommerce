package com.barizi.ecommerce.barizi;

import com.barizi.ecommerce.barizi.Config.JwtService;
import com.barizi.ecommerce.barizi.DTOs.Request.AuthRequests.UserCreationRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.AuthResponses.AuthResponse;
import com.barizi.ecommerce.barizi.Entities.UserEntity;
import com.barizi.ecommerce.barizi.Repositories.UserRepository;
import com.barizi.ecommerce.barizi.Services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;


    private UserCreationRequest userCreationRequest;

    @BeforeEach
    void setUp() {
        userCreationRequest = new UserCreationRequest();
        userCreationRequest.setUserName("testuser");
        userCreationRequest.setEmail("test@example.com");
        userCreationRequest.setPhoneNumber("+12345678901");
        userCreationRequest.setPassword("securepassword");
        userCreationRequest.setFirstName("Test");
        userCreationRequest.setLastName("User");
        userCreationRequest.setRole("CUSTOMER");
    }

    @Test
    void testIsValidPhoneNumber_Valid() {
        assertTrue(AuthService.isValidPhoneNumber("+12345678901"));
    }

    @Test
    void testIsValidPhoneNumber_Invalid() {
        assertFalse(AuthService.isValidPhoneNumber("123"));
        assertFalse(AuthService.isValidPhoneNumber("abc123456789"));
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        when(userRepository.findByUsername(userCreationRequest.getUserName())).thenReturn(Optional.of(new UserEntity()));
        ResponseEntity<AuthResponse> response = authService.registerUser(userCreationRequest);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("User with that username already exists", response.getBody().getMessage());
    }

    @Test
    void testRegisterUser_InvalidPhoneNumber() {
        userCreationRequest.setPhoneNumber("invalid_phone");
        ResponseEntity<AuthResponse> response = authService.registerUser(userCreationRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Please enter a valid phone number", response.getBody().getMessage());
    }


}

