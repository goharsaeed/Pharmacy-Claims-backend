# Pharmacy-Claims-backend
# Pharmacy Claims Processing System

## Overview
A scalable, production-grade pharmacy claims processing system built with Spring Boot, Kafka, JPA, Drools, Docker, and Kubernetes.

## Features
- Accepts pharmacy claims via Kafka
- Applies business rules (Drools)
- Stores claims in PostgreSQL
- Publishes processed/error claims to Kafka
- Handles retries for failed claims
- Generates daily and invoice-style reports

## Tech Stack
- Java 17, Spring Boot 3.x
- Spring Data JPA (PostgreSQL)
- Spring Kafka
- Drools (rules engine)
- Docker, Kubernetes

## Build & Run (Locally)
1. **Build the app:**
   ```sh
   mvn clean package -DskipTests
   ```
2. **Run the app:**
   ```sh
   java -jar target/pharmacy-claims-processing-0.0.1-SNAPSHOT.jar
   ```

## Docker
1. **Build Docker image:**
   ```sh
   docker build -t pharmacy-claims-processing:latest .
   ```
2. **Run Docker container:**
   ```sh
   docker run -p 8080:8080 pharmacy-claims-processing:latest
   ```

## Kubernetes
1. **Apply ConfigMap:**
   ```sh
   kubectl apply -f k8s/configmap.yaml
   ```
2. **Apply Deployment:**
   ```sh
   kubectl apply -f k8s/deployment.yaml
   ```
3. **Apply Service:**
   ```sh
   kubectl apply -f k8s/service.yaml
   ```

## Example Kafka Message (JSON)
Send to topic `pharmacy-claims`:
```json
{
  "claimId": 1,
  "patientId": 1,
  "pharmacyId": 1,
  "insuranceId": 1,
  "claimType": "PRESCRIPTION",
  "claimCost": 500.00
}
```

## Reports
- Daily and invoice-style reports are logged automatically.

## Notes
- Configure DB/Kafka endpoints as needed in `application.yml` or via Kubernetes ConfigMap.
- Retry count is configurable.

--- 