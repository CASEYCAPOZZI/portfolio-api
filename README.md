# Portfolio API (`portfolio-api`)

## Overview

This repository contains the **Java Spring Boot API** backing my portfolio site.

- **Production base URL**: `https://api.caseycapozzi.com`
- **Frontend**: Next.js deployed on AWS Amplify at `https://caseycapozzi.com`
- **Hosting**: AWS ECS (API) + Route 53 (DNS)

## Tech Stack

- Java (see `pom.xml`)
- Spring Boot (WebMVC)
- OpenAPI/Swagger UI via `springdoc-openapi`
- Lombok (annotation processing)

## Running Locally

### Prerequisites

- Java (matching the version in `pom.xml`)
- Maven

### Start the API

```bash
./mvnw spring-boot:run
```

The API will start on the default Spring Boot port unless overridden.

## Key Endpoints

- **Health check**: `GET /health`
- **Status**: `GET /api/v1/status`

Example:

```bash
curl http://localhost:8080/api/v1/status
```

## API Docs (Swagger / OpenAPI)

With the app running locally:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

Requires `springdoc-openapi` 3.x with Spring Boot 4 (see `pom.xml`).

## Notes on Configuration

- `src/main/resources/application.yaml` currently excludes `DataSourceAutoConfiguration` (no DB wired by default).
- CORS is configured in `src/main/java/com/caseycapozzi/portfolioapi/config/CorsConfig.java` and allows:
  - `http://localhost:3000`
  - `https://caseycapozzi.com`
  - `https://www.caseycapozzi.com`

## Deployment

This service is deployed to **AWS ECS** and exposed publicly via `https://api.caseycapozzi.com`.

