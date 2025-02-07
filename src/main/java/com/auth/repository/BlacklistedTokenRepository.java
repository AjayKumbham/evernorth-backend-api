package com.auth.repository;

import com.auth.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {
    @Modifying
    @Query("DELETE FROM BlacklistedToken t WHERE t.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}