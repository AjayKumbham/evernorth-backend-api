package com.auth.service;

import com.auth.dto.AllergyRecordRequest;
import com.auth.dto.AllergyRecordResponse;
import com.auth.dto.UpdateAllergyRecordRequest;
import com.auth.exception.ResourceNotFoundException;
import com.auth.model.AllergyRecord;
import com.auth.model.AllergyRecordId;
import com.auth.repository.AllergyRecordRepository;
import com.auth.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllergyRecordService {

    private final AllergyRecordRepository allergyRecordRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<AllergyRecordResponse> getCurrentUserAllergyRecords() {
        String memberId = securityUtils.getCurrentUserId();
        List<AllergyRecord> allergyRecords = allergyRecordRepository.findByMemberIdOrderByRecordNoAsc(memberId);
        
        if (allergyRecords.isEmpty()) {
            throw new ResourceNotFoundException("No allergy records found for the user");
        }
        
        return allergyRecords.stream()
                .map(this::mapToAllergyRecordResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AllergyRecordResponse addAllergyRecord(AllergyRecordRequest request) {
        String memberId = securityUtils.getCurrentUserId();
        
        Integer nextRecordNo = allergyRecordRepository.findByMemberIdOrderByRecordNoAsc(memberId)
                .stream()
                .map(AllergyRecord::getRecordNo)
                .max(Integer::compareTo)
                .orElse(0) + 1;
        
        AllergyRecord allergyRecord = AllergyRecord.builder()
                .memberId(memberId)
                .recordNo(nextRecordNo)
                .allergies(request.getAllergies())
                .description(request.getDescription())
                .build();
        
        AllergyRecord savedRecord = allergyRecordRepository.save(allergyRecord);
        return mapToAllergyRecordResponse(savedRecord);
    }

    @Transactional
    public AllergyRecordResponse updateAllergyRecord(Integer recordNo, UpdateAllergyRecordRequest request) {
        String memberId = securityUtils.getCurrentUserId();
        AllergyRecordId allergyRecordId = new AllergyRecordId(memberId, recordNo);
        
        AllergyRecord allergyRecord = allergyRecordRepository.findById(allergyRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Allergy record not found"));
        
        if (request.getAllergies() != null) allergyRecord.setAllergies(request.getAllergies());
        if (request.getDescription() != null) allergyRecord.setDescription(request.getDescription());
        
        AllergyRecord updatedRecord = allergyRecordRepository.save(allergyRecord);
        return mapToAllergyRecordResponse(updatedRecord);
    }

    @Transactional
    public void deleteAllergyRecord(Integer recordNo) {
        String memberId = securityUtils.getCurrentUserId();
        AllergyRecordId allergyRecordId = new AllergyRecordId(memberId, recordNo);
        
        if (!allergyRecordRepository.existsById(allergyRecordId)) {
            throw new ResourceNotFoundException("Allergy record not found");
        }
        
        allergyRecordRepository.deleteById(allergyRecordId);
    }
    
    private AllergyRecordResponse mapToAllergyRecordResponse(AllergyRecord allergyRecord) {
        return AllergyRecordResponse.builder()
                .recordNo(allergyRecord.getRecordNo())
                .allergies(allergyRecord.getAllergies())
                .description(allergyRecord.getDescription())
                .build();
    }
}