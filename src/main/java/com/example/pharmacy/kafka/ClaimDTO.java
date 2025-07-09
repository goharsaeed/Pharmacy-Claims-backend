package com.example.pharmacy.kafka;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDTO {
    private Long claimId;
    private Long patientId;
    private Long pharmacyId;
    private Long insuranceId;
    private String claimType;
    private BigDecimal claimCost;
} 