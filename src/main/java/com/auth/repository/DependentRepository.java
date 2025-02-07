package com.auth.repository;

import com.auth.model.Dependent;
import com.auth.model.DependentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DependentRepository extends JpaRepository<Dependent, DependentId> {
    List<Dependent> findByMemberId(String memberId);
}