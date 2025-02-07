package com.auth.repository;

import com.auth.model.Payment;
import com.auth.model.PaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, PaymentId> {
    List<Payment> findByMemberId(String memberId);
}