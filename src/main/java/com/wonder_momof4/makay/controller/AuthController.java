package com.wonder_momof4.makay.controller;


import com.wonder_momof4.makay.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private  AuthenticationManager authManager;
    private  JwtUtil jwtUtil;
    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );
            return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(req.email())));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Email ou mot de passe incorrect"));
        }
    }

    record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    record LoginResponse(String token) {}
    record ErrorResponse(String message) {}
}