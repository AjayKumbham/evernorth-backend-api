package com.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment")
@IdClass(PaymentId.class)
public class Payment {
    
    @Id
    @Column(name = "member_id")
    private String memberId;
    
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;
    
    @Column(name = "card_number", length = 16)
    private String cardNumber;
    
    @Column(name = "upi_id", length = 50)
    private String upiId;
    
    @Column(name = "name_on_card", length = 100)
    private String nameOnCard;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardType cardType;
    
    @Column(name = "account_holder_name", length = 100)
    private String accountHolderName;
    
    @Column(name = "bank_account_number", length = 20)
    private String bankAccountNumber;
    
    @Column(name = "ifsc_code", length = 11)
    private String ifscCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;
}