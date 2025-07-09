package com.example.pharmacy.service;

import com.example.pharmacy.domain.ErrorClaim;

public interface ClaimRetryHandler {
    void handleRetry(ErrorClaim errorClaim);
} 