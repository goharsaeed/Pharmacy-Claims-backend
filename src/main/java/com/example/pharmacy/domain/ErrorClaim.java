package com.example.pharmacy.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String claimData; // Store the claim as JSON or String

    private String errorMessage;

    private int retryCount;

    private LocalDateTime createdAt;
} 