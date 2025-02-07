package com.auth.dto;

import com.auth.model.CardType;
import com.auth.model.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private PaymentType paymentType;
    private String maskedCardNumber;
    private String upiId;
    private String nameOnCard;
    private LocalDate expiryDate;
    private CardType cardType;
    private String accountHolderName;
    private String maskedBankAccountNumber;
    private String ifscCode;
}