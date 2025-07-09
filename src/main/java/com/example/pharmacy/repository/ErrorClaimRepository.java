package com.example.pharmacy.repository;

import com.example.pharmacy.domain.ErrorClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorClaimRepository extends JpaRepository<ErrorClaim, Long> {
} 