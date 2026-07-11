package org.example.forum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.forum.dto.LoginRequest;
import org.example.forum.dto.RegisterRequest;
import org.example.forum.dto.AuthResponse;
import org.example.forum.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
