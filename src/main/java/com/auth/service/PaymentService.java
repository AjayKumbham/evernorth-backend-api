package com.auth.service;

import com.auth.dto.PaymentRequest;
import com.auth.dto.PaymentResponse;
import com.auth.dto.UpdatePaymentRequest;
import com.auth.exception.ResourceNotFoundException;
import com.auth.model.Payment;
import com.auth.model.PaymentId;
import com.auth.model.PaymentType;
import com.auth.repository.PaymentRepository;
import com.auth.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<PaymentResponse> getCurrentUserPayments() {
        String memberId = securityUtils.getCurrentUserId();
        List<Payment> payments = paymentRepository.findByMemberId(memberId);
        
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payment methods found for the user");
        }
        
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse addPayment(PaymentRequest request) {
        String memberId = securityUtils.getCurrentUserId();
        
        Payment payment = Payment.builder()
                .memberId(memberId)
                .paymentType(request.getPaymentType())
                .cardNumber(request.getCardNumber())
                .upiId(request.getUpiId())
                .nameOnCard(request.getNameOnCard())
                .expiryDate(request.getExpiryDate())
                .cardType(request.getCardType())
                .accountHolderName(request.getAccountHolderName())
                .bankAccountNumber(request.getBankAccountNumber())
                .ifscCode(request.getIfscCode())
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        return mapToPaymentResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse updatePayment(String paymentType, UpdatePaymentRequest request) {
        String memberId = securityUtils.getCurrentUserId();
        PaymentId paymentId = new PaymentId(memberId, PaymentType.valueOf(paymentType.toLowerCase()));
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
        
        if (request.getCardNumber() != null) payment.setCardNumber(request.getCardNumber());
        if (request.getUpiId() != null) payment.setUpiId(request.getUpiId());
        if (request.getNameOnCard() != null) payment.setNameOnCard(request.getNameOnCard());
        if (request.getExpiryDate() != null) payment.setExpiryDate(request.getExpiryDate());
        if (request.getCardType() != null) payment.setCardType(request.getCardType());
        if (request.getAccountHolderName() != null) payment.setAccountHolderName(request.getAccountHolderName());
        if (request.getBankAccountNumber() != null) payment.setBankAccountNumber(request.getBankAccountNumber());
        if (request.getIfscCode() != null) payment.setIfscCode(request.getIfscCode());
        
        Payment updatedPayment = paymentRepository.save(payment);
        return mapToPaymentResponse(updatedPayment);
    }

    @Transactional
    public void deletePayment(String paymentType) {
        String memberId = securityUtils.getCurrentUserId();
        PaymentId paymentId = new PaymentId(memberId, PaymentType.valueOf(paymentType.toLowerCase()));
        
        if (!paymentRepository.existsById(paymentId)) {
            throw new ResourceNotFoundException("Payment method not found");
        }
        
        paymentRepository.deleteById(paymentId);
    }
    
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentType(payment.getPaymentType())
                .maskedCardNumber(maskCardNumber(payment.getCardNumber()))
                .upiId(payment.getUpiId())
                .nameOnCard(payment.getNameOnCard())
                .expiryDate(payment.getExpiryDate())
                .cardType(payment.getCardType())
                .accountHolderName(payment.getAccountHolderName())
                .maskedBankAccountNumber(maskBankAccountNumber(payment.getBankAccountNumber()))
                .ifscCode(payment.getIfscCode())
                .build();
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        int length = cardNumber.length();
        if (length <= 4) {
            return cardNumber;
        }
        return "*".repeat(length - 4) + cardNumber.substring(length - 4);
    }
    
    private String maskBankAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        int length = accountNumber.length();
        if (length <= 4) {
            return accountNumber;
        }
        return "*".repeat(length - 4) + accountNumber.substring(length - 4);
    }
}