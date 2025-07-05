package com.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.AuthenticationRequest;
import com.auth.dto.AuthenticationResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;
import com.auth.service.AuthenticationService;
import com.auth.security.JwtService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        authenticationService.register(request);
        return ResponseEntity.ok("Registration initiated. Please verify your email with the OTP sent.");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthenticationResponse> verifyEmail(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.verifyEmail(request);
        
        // Get memberId from the service and generate JWT token
        String memberId = authenticationService.getMemberIdFromEmail(request.getEmail());
        String token = jwtService.generateToken(memberId);
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(86400) // 24 hours
                .path("/")
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/login/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody LoginRequest request) throws MessagingException {
        authenticationService.sendOtp(request);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<AuthenticationResponse> verifyOtp(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.verifyOtp(request);
        
        // Get memberId from the service and generate JWT token
        String memberId = authenticationService.getMemberIdFromEmail(request.getEmail());
        String token = jwtService.generateToken(memberId);
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(86400) // 24 hours
                .path("/")
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authenticationService.logout(token.substring(7));
        
        // Clear the JWT cookie
        ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(0) // Expire immediately
                .path("/")
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }
}