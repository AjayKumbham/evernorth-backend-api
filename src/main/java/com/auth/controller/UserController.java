package com.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.AddressRequest;
import com.auth.dto.AddressResponse;
import com.auth.dto.AllergyRecordRequest;
import com.auth.dto.AllergyRecordResponse;
import com.auth.dto.DependentRequest;
import com.auth.dto.DependentResponse;
import com.auth.dto.EmailUpdateVerificationRequest;
import com.auth.dto.HealthRecordRequest;
import com.auth.dto.HealthRecordResponse;
import com.auth.dto.PaymentRequest;
import com.auth.dto.PaymentResponse;
import com.auth.dto.UpdateAddressRequest;
import com.auth.dto.UpdateAllergyRecordRequest;
import com.auth.dto.UpdateDependentRequest;
import com.auth.dto.UpdateHealthRecordRequest;
import com.auth.dto.UpdatePaymentRequest;
import com.auth.dto.UpdateProfileRequest;
import com.auth.dto.UserProfileResponse;
import com.auth.service.AddressService;
import com.auth.service.AllergyRecordService;
import com.auth.service.DependentService;
import com.auth.service.HealthRecordService;
import com.auth.service.PaymentService;
import com.auth.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PaymentService paymentService;
    private final AddressService addressService;
    private final DependentService dependentService;
    private final HealthRecordService healthRecordService;
    private final AllergyRecordService allergyRecordService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @PostMapping("/profile/verify-email")
    public ResponseEntity<String> sendEmailVerification(@Valid @RequestBody EmailUpdateVerificationRequest request) 
            throws MessagingException {
        userService.sendEmailVerification(request.getEmail());
        return ResponseEntity.ok("Verification email sent successfully");
    }

    @PutMapping("/profile/verify-email")
    public ResponseEntity<UserProfileResponse> verifyEmailUpdate(@Valid @RequestBody EmailUpdateVerificationRequest request) {
        return ResponseEntity.ok(userService.verifyEmailUpdate(request));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> getCurrentUserPayments() {
        return ResponseEntity.ok(paymentService.getCurrentUserPayments());
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> addPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.addPayment(request));
    }

    @PutMapping("/payments/{paymentType}")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable String paymentType,
            @Valid @RequestBody UpdatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.updatePayment(paymentType, request));
    }

    @DeleteMapping("/payments/{paymentType}")
    public ResponseEntity<Void> deletePayment(@PathVariable String paymentType) {
        paymentService.deletePayment(paymentType);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponse>> getCurrentUserAddresses() {
        return ResponseEntity.ok(addressService.getCurrentUserAddresses());
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(request));
    }

    @PutMapping("/addresses/{addressLabel}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable String addressLabel,
            @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(addressLabel, request));
    }

    @DeleteMapping("/addresses/{addressLabel}")
    public ResponseEntity<Void> deleteAddress(@PathVariable String addressLabel) {
        addressService.deleteAddress(addressLabel);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dependents")
    public ResponseEntity<List<DependentResponse>> getCurrentUserDependents() {
        return ResponseEntity.ok(dependentService.getCurrentUserDependents());
    }

    @PostMapping("/dependents")
    public ResponseEntity<DependentResponse> addDependent(@Valid @RequestBody DependentRequest request) {
        return ResponseEntity.ok(dependentService.addDependent(request));
    }

    @PutMapping("/dependents/{fullName}")
    public ResponseEntity<DependentResponse> updateDependent(
            @PathVariable String fullName,
            @Valid @RequestBody UpdateDependentRequest request) {
        return ResponseEntity.ok(dependentService.updateDependent(fullName, request));
    }

    @DeleteMapping("/dependents/{fullName}")
    public ResponseEntity<Void> deleteDependent(@PathVariable String fullName) {
        dependentService.deleteDependent(fullName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health-records")
    public ResponseEntity<List<HealthRecordResponse>> getCurrentUserHealthRecords() {
        return ResponseEntity.ok(healthRecordService.getCurrentUserHealthRecords());
    }

    @PostMapping("/health-records")
    public ResponseEntity<HealthRecordResponse> addHealthRecord(@Valid @RequestBody HealthRecordRequest request) {
        return ResponseEntity.ok(healthRecordService.addHealthRecord(request));
    }

    @PutMapping("/health-records/{recordNo}")
    public ResponseEntity<HealthRecordResponse> updateHealthRecord(
            @PathVariable Integer recordNo,
            @Valid @RequestBody UpdateHealthRecordRequest request) {
        return ResponseEntity.ok(healthRecordService.updateHealthRecord(recordNo, request));
    }

    @DeleteMapping("/health-records/{recordNo}")
    public ResponseEntity<Void> deleteHealthRecord(@PathVariable Integer recordNo) {
        healthRecordService.deleteHealthRecord(recordNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/allergy-records")
    public ResponseEntity<List<AllergyRecordResponse>> getCurrentUserAllergyRecords() {
        return ResponseEntity.ok(allergyRecordService.getCurrentUserAllergyRecords());
    }

    @PostMapping("/allergy-records")
    public ResponseEntity<AllergyRecordResponse> addAllergyRecord(@Valid @RequestBody AllergyRecordRequest request) {
        return ResponseEntity.ok(allergyRecordService.addAllergyRecord(request));
    }

    @PutMapping("/allergy-records/{recordNo}")
    public ResponseEntity<AllergyRecordResponse> updateAllergyRecord(
            @PathVariable Integer recordNo,
            @Valid @RequestBody UpdateAllergyRecordRequest request) {
        return ResponseEntity.ok(allergyRecordService.updateAllergyRecord(recordNo, request));
    }

    @DeleteMapping("/allergy-records/{recordNo}")
    public ResponseEntity<Void> deleteAllergyRecord(@PathVariable Integer recordNo) {
        allergyRecordService.deleteAllergyRecord(recordNo);
        return ResponseEntity.ok().build();
    }
}