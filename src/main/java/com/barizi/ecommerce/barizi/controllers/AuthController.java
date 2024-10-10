package com.barizi.ecommerce.barizi.controllers;

import com.barizi.ecommerce.barizi.DTOs.Request.LoginRequest;
import com.barizi.ecommerce.barizi.DTOs.Request.UserCreationRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.AuthResponse;
import com.barizi.ecommerce.barizi.Entities.UserEntity;
import com.barizi.ecommerce.barizi.Services.AuthService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/api/v1")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody @NotNull UserCreationRequest user){
//        log.info("role ==============="+user.getRole());
        return service.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @NotNull LoginRequest user){
        return service.login(user);
    }

}
