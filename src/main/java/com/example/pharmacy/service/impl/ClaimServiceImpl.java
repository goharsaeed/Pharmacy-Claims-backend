package com.example.pharmacy.service.impl;

import com.example.pharmacy.domain.Claim;
import com.example.pharmacy.domain.ErrorClaim;
import com.example.pharmacy.repository.ClaimRepository;
import com.example.pharmacy.repository.ErrorClaimRepository;
import com.example.pharmacy.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {
    private final ClaimRepository claimRepository;
    private final ErrorClaimRepository errorClaimRepository;

    @Override
    @Transactional
    public Claim processClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    @Override
    @Transactional
    public void markClaimStatus(Long claimId, String status) {
        Claim claim = claimRepository.findById(claimId).orElseThrow();
        claim.setStatus(status);
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    public void saveErrorClaim(ErrorClaim errorClaim) {
        errorClaimRepository.save(errorClaim);
    }

    @Override
    public Claim getClaimById(Long id) {
        return claimRepository.findById(id).orElse(null);
    }
} 