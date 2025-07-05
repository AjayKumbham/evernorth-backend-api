package com.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.auth.dto.AuthenticationRequest;
import com.auth.dto.AuthenticationResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.LogoutResponse;
import com.auth.dto.RegisterRequest;
import com.auth.service.AuthenticationService;
import java.time.LocalDateTime;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        authenticationService.register(request);
        return ResponseEntity.ok("Registration initiated. Please verify your email with the OTP sent.");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthenticationResponse> verifyEmail(
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.verifyEmail(request, response));
    }

    @PostMapping("/login/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody LoginRequest request) throws MessagingException {
        authenticationService.sendOtp(request);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<AuthenticationResponse> verifyOtp(
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.verifyOtp(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        authenticationService.logout(request, response);
        return ResponseEntity.ok(LogoutResponse.builder()
                .message("Logged out successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
}