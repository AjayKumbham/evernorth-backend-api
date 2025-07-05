package com.auth.service;

import com.auth.dto.*;
import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.OtpValidationException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.model.EmailVerification;
import com.auth.model.Member;
import com.auth.repository.EmailVerificationRepository;
import com.auth.repository.MemberRepository;
import com.auth.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.SecureRandom;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberRepository memberRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final MemberIdGenerator memberIdGenerator;
    private final RateLimiter rateLimiter;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Transactional
    public void register(RegisterRequest request) throws MessagingException {
        // Quick check for existing email
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidCredentialsException("Email already registered");
        }

        String otp = generateOtp();
        EmailVerification verification = EmailVerification.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .contact(request.getContact())
                .dob(request.getDob())
                .otp(passwordEncoder.encode(otp))
                .otpExpiryTime(LocalDateTime.now().plusMinutes(5))
                .build();
        
        // Save verification and send email in parallel
        emailVerificationRepository.save(verification);
        emailService.sendVerificationEmail(request.getEmail(), otp);
    }

    @Transactional
    public AuthenticationResponse verifyEmail(AuthenticationRequest request, HttpServletResponse response) {
        EmailVerification verification = emailVerificationRepository.findById(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Registration request not found"));

        // Validate OTP
        if (verification.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            emailVerificationRepository.delete(verification);
            throw new OtpValidationException("OTP has expired. Please register again.");
        }

        if (!passwordEncoder.matches(request.getOtp(), verification.getOtp())) {
            throw new OtpValidationException("Invalid OTP");
        }

        // Create member
        String memberId = memberIdGenerator.generateMemberId(verification.getFullName(), verification.getDob());
        Member member = Member.builder()
                .memberId(memberId)
                .fullName(verification.getFullName())
                .email(verification.getEmail())
                .contact(verification.getContact())
                .dob(verification.getDob())
                .build();
        
        // Save member and generate token
        member = memberRepository.save(member);
        emailVerificationRepository.delete(verification);
        
        // Send welcome email asynchronously
        try {
            emailService.sendWelcomeEmail(member.getEmail(), member.getFullName());
        } catch (MessagingException e) {
            // Log error but continue as this is not critical
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        // Generate token but don't return in response body
        String token = jwtService.generateToken(memberId);
        
        // Set HTTP-only cookie
        setJwtCookie(response, token);
        
        return AuthenticationResponse.builder()
                .token(null)
                .build();
    }

    @Transactional
    public void sendOtp(LoginRequest request) throws MessagingException {
        // Rate limiting for OTP generation
        String rateLimitKey = "otp_generation:" + request.getEmail();
        if (!rateLimiter.allowRequest(rateLimitKey, 5, java.time.Duration.ofMinutes(15))) {
            throw new com.auth.exception.RateLimitExceededException("Too many OTP requests. Please try again later.");
        }
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        String otp = generateOtp();
        member.setOtp(passwordEncoder.encode(otp));
        member.setOtpExpiryTime(LocalDateTime.now().plusMinutes(1));
        memberRepository.save(member);
        
        emailService.sendOtpEmail(member.getEmail(), otp);
    }

    @Transactional
    public AuthenticationResponse verifyOtp(AuthenticationRequest request, HttpServletResponse response) {
        // Rate limiting for OTP verification
        String rateLimitKey = "otp_verification:" + request.getEmail();
        if (!rateLimiter.allowRequest(rateLimitKey, 10, java.time.Duration.ofMinutes(15))) {
            throw new com.auth.exception.RateLimitExceededException("Too many verification attempts. Please try again later.");
        }
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (member.getOtpExpiryTime() == null || member.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpValidationException("OTP has expired");
        }

        if (!passwordEncoder.matches(request.getOtp(), member.getOtp())) {
            throw new OtpValidationException("Invalid OTP");
        }

        // Clear OTP data
        member.setOtp(null);
        member.setOtpExpiryTime(null);
        memberRepository.save(member);

        // Generate token but don't return in response body
        String token = jwtService.generateToken(member.getMemberId());
        
        // Set HTTP-only cookie
        setJwtCookie(response, token);
        
        return AuthenticationResponse.builder()
                .token(null)
                .build();
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Extract JWT from cookie
        String jwt = extractJwtFromCookie(request);
        if (jwt == null) {
            throw new InvalidCredentialsException("No authentication token found");
        }
        // Validate token ownership
        String memberId = validateTokenOwnership(jwt);
        // Rate limiting for logout
        String rateLimitKey = "logout:" + memberId;
        if (!rateLimiter.allowRequest(rateLimitKey, 10, java.time.Duration.ofMinutes(15))) {
            throw new com.auth.exception.RateLimitExceededException("Too many logout attempts");
        }
        // Blacklist the token
        tokenBlacklistService.blacklistToken(jwt, LocalDateTime.now().plusDays(1));
        // Clear the JWT cookie
        clearJwtCookie(response);
        // Log logout activity
        logger.info("User {} logged out successfully", memberId);
    }

    private String validateTokenOwnership(String jwt) {
        try {
            String memberId = jwtService.extractMemberId(jwt);
            if (memberId == null) {
                throw new InvalidCredentialsException("Invalid token");
            }
            // Verify token is valid
            UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);
            if (!jwtService.isTokenValid(jwt, userDetails)) {
                throw new InvalidCredentialsException("Invalid token");
            }
            return memberId;
        } catch (Exception e) {
            logger.warn("Token validation failed during logout: {}", e.getMessage());
            throw new InvalidCredentialsException("Invalid token");
        }
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie jwtCookie = new Cookie("jwt_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // HTTPS only
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400); // 24 hours
        jwtCookie.setDomain(null); // Current domain only
        response.addCookie(jwtCookie);
    }

    private void clearJwtCookie(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete cookie
        jwtCookie.setDomain(null);
        response.addCookie(jwtCookie);
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String generateOtp() {
        SecureRandom secureRandom = new SecureRandom();
        return String.format("%06d", secureRandom.nextInt(1000000));
    }
}