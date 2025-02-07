package com.auth.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequest {
    @Size(max = 50, message = "Address label must not exceed 50 characters")
    private String addressLabel;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    @Size(max = 10, message = "ZIP code must not exceed 10 characters")
    private String zipCode;
    private String landmark;
}