package com.example.pharmacy.service.impl;

import com.example.pharmacy.domain.ErrorClaim;
import com.example.pharmacy.kafka.ClaimDTO;
import com.example.pharmacy.kafka.ClaimKafkaProducer;
import com.example.pharmacy.service.ClaimRetryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimRetryHandlerImpl implements ClaimRetryHandler {
    private final ClaimKafkaProducer claimKafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${claims.retry-count:3}")
    private int maxRetryCount;

    @Override
    public void handleRetry(ErrorClaim errorClaim) {
        if (errorClaim.getRetryCount() < maxRetryCount) {
            try {
                // Deserialize claimData to ClaimDTO
                ClaimDTO claimDTO = objectMapper.readValue(errorClaim.getClaimData(), ClaimDTO.class);
                log.info("Retrying claim with id: {} (retry count: {})", claimDTO.getClaimId(), errorClaim.getRetryCount());
                claimKafkaProducer.sendErrorClaim(claimDTO);
            } catch (Exception e) {
                log.error("Failed to retry claim: {}", e.getMessage());
            }
        } else {
            log.error("Max retry count reached for claim. Marking as permanently failed. ErrorClaim id: {}", errorClaim.getId());
        }
    }
} 