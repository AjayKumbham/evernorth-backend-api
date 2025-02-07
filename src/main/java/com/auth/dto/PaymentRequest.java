package com.auth.dto;

import com.auth.model.CardType;
import com.auth.model.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;
    
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;
    
    @Pattern(regexp = "[a-zA-Z0-9.]+@[a-zA-Z0-9.]+", message = "Invalid UPI ID format")
    private String upiId;
    
    @Size(max = 100, message = "Name on card must not exceed 100 characters")
    private String nameOnCard;
    
    private LocalDate expiryDate;
    
    private CardType cardType;
    
    @Size(max = 100, message = "Account holder name must not exceed 100 characters")
    private String accountHolderName;
    
    @Pattern(regexp = "\\d{9,18}", message = "Bank account number must be between 9 and 18 digits")
    private String bankAccountNumber;
    
    @Pattern(regexp = "[A-Z]{4}0[A-Z0-9]{6}", message = "Invalid IFSC code format")
    private String ifscCode;
}