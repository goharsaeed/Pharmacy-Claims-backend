package com.example.pharmacy.service;

import com.example.pharmacy.domain.Claim;

public interface ClaimProcessor {
    Claim applyBusinessRules(Claim claim) throws Exception;
} 