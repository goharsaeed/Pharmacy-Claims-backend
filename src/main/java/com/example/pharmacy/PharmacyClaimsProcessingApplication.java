package com.example.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PharmacyClaimsProcessingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmacyClaimsProcessingApplication.class, args);
    }
} 