# Priceflow

Priceflow is a production-style event-driven pricing and vendor cost management platform built as a personal portfolio project.

## Technology Stack

- Java 21
- Spring Boot
- Spring WebFlux
- Apache Kafka
- PostgreSQL
- MongoDB
- Redis
- Docker
- Kubernetes
- GitHub Actions

## Repository Structure

```text
Priceflow/
├── services/
│   └── cost-request-service/
├── docs/
│   └── architecture/
├── infrastructure/
│   ├── docker/
│   └── kubernetes/
├── .github/
│   └── workflows/
└── README.md
```

## Current Status

The initial `cost-request-service` Spring Boot skeleton is configured and running successfully with Java 21, Maven, WebFlux, Validation, Lombok, and Spring Boot Actuator.

## Planned Capabilities

- Vendor cost change request lifecycle
- Product and catalog lookup
- Approval workflow
- Kafka-based event-driven processing
- Pricing updates
- Notifications
- Authentication and authorization
- Observability
- Containerized local development
- Kubernetes deployment
- CI/CD
