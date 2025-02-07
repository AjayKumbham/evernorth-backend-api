package com.auth.repository;

import com.auth.model.AllergyRecord;
import com.auth.model.AllergyRecordId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergyRecordRepository extends JpaRepository<AllergyRecord, AllergyRecordId> {
    List<AllergyRecord> findByMemberIdOrderByRecordNoAsc(String memberId);
}