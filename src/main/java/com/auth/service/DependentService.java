package com.auth.service;

import com.auth.dto.DependentRequest;
import com.auth.dto.DependentResponse;
import com.auth.dto.UpdateDependentRequest;
import com.auth.exception.ResourceNotFoundException;
import com.auth.model.Dependent;
import com.auth.model.DependentId;
import com.auth.repository.DependentRepository;
import com.auth.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DependentService {

    private final DependentRepository dependentRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<DependentResponse> getCurrentUserDependents() {
        String memberId = securityUtils.getCurrentUserId();
        List<Dependent> dependents = dependentRepository.findByMemberId(memberId);
        
        if (dependents.isEmpty()) {
            throw new ResourceNotFoundException("No dependents found for the user");
        }
        
        return dependents.stream()
                .map(this::mapToDependentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DependentResponse addDependent(DependentRequest request) {
        String memberId = securityUtils.getCurrentUserId();
        
        Dependent dependent = Dependent.builder()
                .memberId(memberId)
                .fullName(request.getFullName())
                .relation(request.getRelation())
                .dob(request.getDob())
                .mobileNumber(request.getMobileNumber())
                .emailAddress(request.getEmailAddress())
                .emergencySosContact(request.getEmergencySosContact())
                .build();
        
        Dependent savedDependent = dependentRepository.save(dependent);
        return mapToDependentResponse(savedDependent);
    }

    @Transactional
    public DependentResponse updateDependent(String fullName, UpdateDependentRequest request) {
        String memberId = securityUtils.getCurrentUserId();
        DependentId dependentId = new DependentId(memberId, fullName);
        
        Dependent dependent = dependentRepository.findById(dependentId)
                .orElseThrow(() -> new ResourceNotFoundException("Dependent not found"));
        
        // Create new dependent with updated name if name is changed
        if (request.getFullName() != null && !request.getFullName().equals(dependent.getFullName())) {
            Dependent newDependent = Dependent.builder()
                    .memberId(memberId)
                    .fullName(request.getFullName())
                    .relation(request.getRelation() != null ? request.getRelation() : dependent.getRelation())
                    .dob(request.getDob() != null ? request.getDob() : dependent.getDob())
                    .mobileNumber(request.getMobileNumber() != null ? request.getMobileNumber() : dependent.getMobileNumber())
                    .emailAddress(request.getEmailAddress() != null ? request.getEmailAddress() : dependent.getEmailAddress())
                    .emergencySosContact(request.getEmergencySosContact() != null ? request.getEmergencySosContact() : dependent.getEmergencySosContact())
                    .build();
            
            dependentRepository.delete(dependent);
            return mapToDependentResponse(dependentRepository.save(newDependent));
        }
        
        // Update other fields if name is not changed
        if (request.getRelation() != null) dependent.setRelation(request.getRelation());
        if (request.getDob() != null) dependent.setDob(request.getDob());
        if (request.getMobileNumber() != null) dependent.setMobileNumber(request.getMobileNumber());
        if (request.getEmailAddress() != null) dependent.setEmailAddress(request.getEmailAddress());
        if (request.getEmergencySosContact() != null) dependent.setEmergencySosContact(request.getEmergencySosContact());
        
        return mapToDependentResponse(dependentRepository.save(dependent));
    }

    @Transactional
    public void deleteDependent(String fullName) {
        String memberId = securityUtils.getCurrentUserId();
        DependentId dependentId = new DependentId(memberId, fullName);
        
        if (!dependentRepository.existsById(dependentId)) {
            throw new ResourceNotFoundException("Dependent not found");
        }
        
        dependentRepository.deleteById(dependentId);
    }
    
    private DependentResponse mapToDependentResponse(Dependent dependent) {
        return DependentResponse.builder()
                .fullName(dependent.getFullName())
                .relation(dependent.getRelation())
                .dob(dependent.getDob())
                .mobileNumber(dependent.getMobileNumber())
                .emailAddress(dependent.getEmailAddress())
                .emergencySosContact(dependent.getEmergencySosContact())
                .build();
    }
}