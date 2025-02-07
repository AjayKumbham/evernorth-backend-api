package com.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async("taskExecutor")
    public void sendVerificationEmail(String to, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("noreply@yourapp.com");
        helper.setTo(to);
        helper.setSubject("Verify Your Email");
        helper.setText("Your email verification OTP is: " + otp + ". This OTP will expire in 5 minutes.");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    public void sendOtpEmail(String to, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("noreply@yourapp.com");
        helper.setTo(to);
        helper.setSubject("Your OTP for Authentication");
        helper.setText("Your OTP is: " + otp + ". This OTP will expire in 1 minute.");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    public void sendWelcomeEmail(String to, String name) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("noreply@yourapp.com");
        helper.setTo(to);
        helper.setSubject("Welcome to Our Application!");
        helper.setText("Dear " + name + ",\n\nWelcome to our application! We're glad to have you on board.");
        
        mailSender.send(message);
    }
}