package com.auth.repository;

import com.auth.model.Address;
import com.auth.model.AddressId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, AddressId> {
    List<Address> findByMemberId(String memberId);
}