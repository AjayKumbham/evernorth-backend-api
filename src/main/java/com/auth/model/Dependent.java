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
@Table(name = "dependents")
@IdClass(DependentId.class)
public class Dependent {
    
    @Id
    @Column(name = "member_id")
    private String memberId;
    
    @Id
    @Column(name = "full_name", length = 100)
    private String fullName;
    
    @Column(nullable = false, length = 50)
    private String relation;
    
    @Column(nullable = false)
    private LocalDate dob;
    
    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;
    
    @Column(name = "email_address", length = 100)
    private String emailAddress;
    
    @Column(name = "emergency_sos_contact")
    private Boolean emergencySosContact;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;
}