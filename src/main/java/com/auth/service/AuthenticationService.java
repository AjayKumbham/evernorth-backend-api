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
    public AuthenticationResponse verifyEmail(AuthenticationRequest request) {
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

        // Generate and return token immediately
        String token = jwtService.generateToken(memberId);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Transactional
    public void sendOtp(LoginRequest request) throws MessagingException {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        String otp = generateOtp();
        member.setOtp(passwordEncoder.encode(otp));
        member.setOtpExpiryTime(LocalDateTime.now().plusMinutes(1));
        memberRepository.save(member);
        
        emailService.sendOtpEmail(member.getEmail(), otp);
    }

    @Transactional
    public AuthenticationResponse verifyOtp(AuthenticationRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (member.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpValidationException("OTP has expired");
        }

        if (!passwordEncoder.matches(request.getOtp(), member.getOtp())) {
            throw new OtpValidationException("Invalid OTP");
        }

        // Clear OTP data
        member.setOtp(null);
        member.setOtpExpiryTime(null);
        memberRepository.save(member);

        // Generate and return token immediately
        String token = jwtService.generateToken(member.getMemberId());
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Transactional
    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token, LocalDateTime.now().plusDays(1));
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}