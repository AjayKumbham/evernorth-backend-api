package com.auth.repository;

import com.auth.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);
    
    @Query("SELECT m.memberId FROM Member m WHERE m.memberId LIKE :prefix% ORDER BY m.memberId DESC")
    Optional<String> findHighestMemberIdByPrefix(@Param("prefix") String prefix);
}