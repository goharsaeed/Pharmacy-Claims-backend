package com.example.pharmacy.kafka;

import com.example.pharmacy.domain.Claim;
import com.example.pharmacy.domain.ErrorClaim;
import com.example.pharmacy.repository.ErrorClaimRepository;
import com.example.pharmacy.service.ClaimProcessor;
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
public class ErrorClaimKafkaListener {

    private final ClaimService claimService;
    private final ClaimProcessor claimProcessor;
    private final ErrorClaimRepository errorClaimRepository;
    private final ObjectMapper objectMapper;

    @Value("${claims.retry-count:3}")
    private int maxRetryCount;

    @KafkaListener(topics = "#{'${kafka.topic.error}'}", groupId = "pharmacy-error-claims-group")
    public void listen(ClaimDTO claimDTO) {
        try {
            // Look up ErrorClaim by matching ClaimId from stored JSON
            ErrorClaim errorClaim = errorClaimRepository.findAll().stream()
                    .filter(ec -> {
                        try {
                            ClaimDTO dto = objectMapper.readValue(ec.getClaimData(), ClaimDTO.class);
                            return dto.getClaimId() != null && dto.getClaimId().equals(claimDTO.getClaimId());
                        } catch (Exception e) {
                            log.warn("Failed to parse ClaimDTO from ErrorClaim row: {}", e.getMessage());
                            return false;
                        }
                    })
                    .findFirst()
                    .orElse(null);

            if (errorClaim == null) {
                log.error("No matching ErrorClaim found for claimId: {}", claimDTO.getClaimId());
                return;
            }

            int retryCount = errorClaim.getRetryCount() + 1;
            errorClaim.setRetryCount(retryCount);

            if (retryCount >= maxRetryCount) {
                errorClaim.setErrorMessage("Max retry count reached");
                errorClaimRepository.save(errorClaim);
                log.error("Retry limit reached for claimId: {}", claimDTO.getClaimId());
                return;
            }

            // Build Claim from DTO for reprocessing
            Claim claim = Claim.builder()
                    .claimType(claimDTO.getClaimType())
                    .claimCost(claimDTO.getClaimCost())
                    .status("RETRYING")
                    .build();

            // TODO: setPatient / setPharmacy / setInsurance if needed
            // claim.setPatient(...);
            // claim.setPharmacy(...);

            Claim processed = claimProcessor.applyBusinessRules(claim);
            claimService.processClaim(processed);
            claimService.markClaimStatus(processed.getId(), processed.getStatus());

            // Cleanup after successful retry
            errorClaimRepository.delete(errorClaim);
            log.info("Claim successfully reprocessed (retry {}): ID {}", retryCount, processed.getId());

        } catch (Exception e) {
            log.error("Error processing retry for claimId {}: {}", claimDTO.getClaimId(), e.getMessage(), e);
        }
    }
}
