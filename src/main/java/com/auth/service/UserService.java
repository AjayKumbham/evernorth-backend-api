package com.auth.service;

import com.auth.dto.EmailUpdateVerificationRequest;
import com.auth.dto.UpdateProfileRequest;
import com.auth.dto.UserProfileResponse;
import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.OtpValidationException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.model.Member;
import com.auth.repository.MemberRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getCurrentUserProfile() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findById(memberId)
                .map(this::mapToUserProfileResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getEmail() != null && !request.getEmail().equals(member.getEmail())) {
            throw new InvalidCredentialsException("Email update requires verification. Please use the email verification endpoint.");
        }

        if (request.getFullName() != null) member.setFullName(request.getFullName());
        if (request.getContact() != null) member.setContact(request.getContact());
        if (request.getDob() != null) member.setDob(request.getDob());

        return mapToUserProfileResponse(memberRepository.save(member));
    }

    @Transactional
    public void sendEmailVerification(String newEmail) throws MessagingException {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new InvalidCredentialsException("Email is required");
        }

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if email is already taken by another user
        if (memberRepository.findByEmail(newEmail)
                .filter(m -> !m.getMemberId().equals(memberId))
                .isPresent()) {
            throw new InvalidCredentialsException("Email already registered");
        }

        String otp = generateOtp();
        member.setOtp(passwordEncoder.encode(otp));
        member.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        memberRepository.save(member);

        // Send email asynchronously
        emailService.sendVerificationEmail(newEmail, otp);
    }

    @Transactional
    public UserProfileResponse verifyEmailUpdate(EmailUpdateVerificationRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidCredentialsException("Email is required");
        }

        if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
            throw new OtpValidationException("OTP is required");
        }

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (member.getOtpExpiryTime() == null || member.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpValidationException("OTP has expired. Please request a new one.");
        }

        if (!passwordEncoder.matches(request.getOtp(), member.getOtp())) {
            throw new OtpValidationException("Invalid OTP");
        }

        // Check if email is already taken by another user
        if (memberRepository.findByEmail(request.getEmail())
                .filter(m -> !m.getMemberId().equals(memberId))
                .isPresent()) {
            throw new InvalidCredentialsException("Email already registered");
        }

        member.setEmail(request.getEmail());
        member.setOtp(null);
        member.setOtpExpiryTime(null);

        return mapToUserProfileResponse(memberRepository.save(member));
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    private UserProfileResponse mapToUserProfileResponse(Member member) {
        return UserProfileResponse.builder()
                .memberId(member.getMemberId())
                .fullName(member.getFullName())
                .email(member.getEmail())
                .contact(member.getContact())
                .dob(member.getDob())
                .createdAt(member.getCreatedAt())
                .build();
    }
}