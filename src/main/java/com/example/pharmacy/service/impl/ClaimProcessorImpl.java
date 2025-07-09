package com.example.pharmacy.service.impl;

import com.example.pharmacy.domain.Claim;
import com.example.pharmacy.service.ClaimProcessor;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClaimProcessorImpl implements ClaimProcessor {
    private final KieSession kieSession;

    @Override
    public Claim applyBusinessRules(Claim claim) throws Exception {
        kieSession.insert(claim);
        kieSession.fireAllRules();
        return claim;
    }
} 