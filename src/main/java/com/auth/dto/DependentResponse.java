package com.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DependentResponse {
    private String fullName;
    private String relation;
    private LocalDate dob;
    private String mobileNumber;
    private String emailAddress;
    private Boolean emergencySosContact;
}