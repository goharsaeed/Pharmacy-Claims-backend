package com.example.pharmacy.kafka;

import com.example.pharmacy.domain.Claim;
import com.example.pharmacy.domain.ErrorClaim;
import com.example.pharmacy.service.ClaimProcessor;
import com.example.pharmacy.service.ClaimRetryHandler;
import com.example.pharmacy.service.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimKafkaListener {
    private final ClaimService claimService;
    private final ClaimProcessor claimProcessor;
    private final ClaimRetryHandler claimRetryHandler;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.claims}")
    private String claimsTopic;

    @KafkaListener(topics = "#{'${kafka.topic.claims}'}", groupId = "pharmacy-claims-group")
    public void listen(ClaimDTO claimDTO) {
        try {
            // Map DTO to entity (in real app, fetch Patient, Pharmacy, Insurance by ID)
            Claim claim = Claim.builder()
                    .claimType(claimDTO.getClaimType())
                    .claimCost(claimDTO.getClaimCost())
                    .status("RECEIVED")
                    .build();
            // TODO: set patient, pharmacy, insurance from repositories
            Claim processed = claimProcessor.applyBusinessRules(claim);
            claimService.processClaim(processed);
            claimService.markClaimStatus(processed.getId(), "PROCESSED");
            log.info("Claim processed: {}", processed);
        } catch (Exception e) {
            log.error("Error processing claim: {}", e.getMessage());
            try {
                String claimJson = objectMapper.writeValueAsString(claimDTO);
                ErrorClaim errorClaim = ErrorClaim.builder()
                        .claimData(claimJson)
                        .errorMessage(e.getMessage())
                        .retryCount(0)
                        .build();
                claimService.saveErrorClaim(errorClaim);
                claimRetryHandler.handleRetry(errorClaim);
            } catch (Exception ex) {
                log.error("Error saving error claim: {}", ex.getMessage());
            }
        }
    }
} 