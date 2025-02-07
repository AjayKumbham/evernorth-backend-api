package com.auth.repository;

import com.auth.model.HealthRecord;
import com.auth.model.HealthRecordId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, HealthRecordId> {
    List<HealthRecord> findByMemberIdOrderByRecordNoAsc(String memberId);
}