package com.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "\\d{10}", message = "Contact must be 10 digits")
    private String contact;
    private LocalDate dob;
}