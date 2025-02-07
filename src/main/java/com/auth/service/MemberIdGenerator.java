package com.auth.service;

import com.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberIdGenerator {
    
    private final MemberRepository memberRepository;
    
    public String generateMemberId(String fullName, LocalDate dob) {
        // Extract first initial
        String firstInitial = fullName.substring(0, 1).toUpperCase();
        
        // Extract last two digits of birth year
        String yearDigits = String.valueOf(dob.getYear()).substring(2);
        
        // Find the highest sequential ID for this combination
        String prefix = firstInitial + yearDigits;
        Optional<String> highestId = memberRepository.findHighestMemberIdByPrefix(prefix);
        
        int sequentialId = highestId
                .map(id -> Integer.parseInt(id.substring(3)) + 1)
                .orElse(1);
        
        // Format sequential ID with leading zeros
        return String.format("%s%02d", prefix, sequentialId);
    }
}