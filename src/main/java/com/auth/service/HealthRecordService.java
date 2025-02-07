package com.auth.service;

import com.auth.dto.HealthRecordRequest;
import com.auth.dto.HealthRecordResponse;
import com.auth.dto.UpdateHealthRecordRequest;
import com.auth.exception.ResourceNotFoundException;
import com.auth.model.HealthRecord;
import com.auth.model.HealthRecordId;
import com.auth.repository.HealthRecordRepository;
import com.auth.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<HealthRecordResponse> getCurrentUserHealthRecords() {
        String memberId = securityUtils.getCurrentUserId();
        List<HealthRecord> healthRecords = healthRecordRepository.findByMemberIdOrderByRecordNoAsc(memberId);
        
        if (healthRecords.isEmpty()) {
            throw new ResourceNotFoundException("No health records found for the user");
        }
        
        return healthRecords.stream()
                .map(this::mapToHealthRecordResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HealthRecordResponse addHealthRecord(HealthRecordRequest request) {
        String memberId = securityUtils.getCurrentUserId();
        
        Integer nextRecordNo = healthRecordRepository.findByMemberIdOrderByRecordNoAsc(memberId)
                .stream()
                .map(HealthRecord::getRecordNo)
                .max(Integer::compareTo)
                .orElse(0) + 1;
        
        HealthRecord healthRecord = HealthRecord.builder()
                .memberId(memberId)
                .recordNo(nextRecordNo)
                .healthCondition(request.getHealthCondition())
                .description(request.getDescription())
                .build();
        
        HealthRecord savedRecord = healthRecordRepository.save(healthRecord);
        return mapToHealthRecordResponse(savedRecord);
    }

    @Transactional
    public HealthRecordResponse updateHealthRecord(Integer recordNo, UpdateHealthRecordRequest request) {
        String memberId = securityUtils.getCurrentUserId();
        HealthRecordId healthRecordId = new HealthRecordId(memberId, recordNo);
        
        HealthRecord healthRecord = healthRecordRepository.findById(healthRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Health record not found"));
        
        if (request.getHealthCondition() != null) healthRecord.setHealthCondition(request.getHealthCondition());
        if (request.getDescription() != null) healthRecord.setDescription(request.getDescription());
        
        HealthRecord updatedRecord = healthRecordRepository.save(healthRecord);
        return mapToHealthRecordResponse(updatedRecord);
    }

    @Transactional
    public void deleteHealthRecord(Integer recordNo) {
        String memberId = securityUtils.getCurrentUserId();
        HealthRecordId healthRecordId = new HealthRecordId(memberId, recordNo);
        
        if (!healthRecordRepository.existsById(healthRecordId)) {
            throw new ResourceNotFoundException("Health record not found");
        }
        
        healthRecordRepository.deleteById(healthRecordId);
    }
    
    private HealthRecordResponse mapToHealthRecordResponse(HealthRecord healthRecord) {
        return HealthRecordResponse.builder()
                .recordNo(healthRecord.getRecordNo())
                .healthCondition(healthRecord.getHealthCondition())
                .description(healthRecord.getDescription())
                .build();
    }
}