package com.auth.dto;

import jakarta.validation.constraints.Email;
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
public class UpdateDependentRequest {
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;
    private String relation;
    private LocalDate dob;
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
    private String mobileNumber;
    @Email(message = "Invalid email format")
    private String emailAddress;
    private Boolean emergencySosContact;
}