package com.example.KYC.project.controller;

import com.example.KYC.project.dto.AuthResponse;
import com.example.KYC.project.dto.LoginRequest;
import com.example.KYC.project.dto.RegisterRequest;
import com.example.KYC.project.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}