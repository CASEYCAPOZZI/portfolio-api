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

| Endpoint | Description |
|----------|-------------|
| `GET /health` | Health check (`UP`) |
| `GET /api/v1/status` | Status for frontend footer / playground |
| `GET /api/fishing/brule/status` | Bois Brule bite score (USGS + NWS) |
| `GET /api/fishing/colfax/status` | Red Cedar at Colfax bite score |

Examples:

```bash
curl http://localhost:8080/health
curl http://localhost:8080/api/v1/status
curl http://localhost:8080/api/fishing/brule/status
```

Fishing endpoints call external USGS/NWS services and may take **several seconds** to respond.

## API Docs (Swagger / OpenAPI)

With the app running locally:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

**Spring Boot 4 requires `springdoc-openapi` 3.x** (currently `3.0.3` in `pom.xml`). Version 2.x will not work on Boot 4.

## Tests

```bash
./mvnw test
```

Controller, bite-predictor, and context-load tests run in CI before every Docker build.

## Notes on Configuration

- `src/main/resources/application.yaml` currently excludes `DataSourceAutoConfiguration` (no DB wired by default).
- CORS is configured in `src/main/java/com/caseycapozzi/portfolioapi/config/CorsConfig.java` and allows:
  - `http://localhost:3000`
  - `https://caseycapozzi.com`
  - `https://www.caseycapozzi.com`

## Deployment & smoke tests

**Hosting:** GitLab CI builds a Docker image, pushes to **ECR**, and deploys to **ECS** (`portfolio-api-5b60` on cluster `default`). Public URL: `https://api.caseycapozzi.com` (ALB).

**Pipeline** (`.gitlab-ci.yml` on `master`):

1. `unit-tests` — `./mvnw test`
2. `build-image` — `docker build` / push to ECR
3. `deploy-ecs` — force new ECS deployment → wait for stable → **`scripts/smoke-api.py`**

The smoke script retries for ~3 minutes and checks:

- `GET /health` returns `UP`
- `GET /api/v1/status` returns JSON
- `GET /api/fishing/brule/status` and `/colfax/status` return `biteScore` between 0 and 100

**Manual smoke after deploy:**

```bash
python3 scripts/smoke-api.py
```

### Production troubleshooting

| Symptom | Likely cause |
|---------|----------------|
| `https://api.caseycapozzi.com/health` returns **503 HTML** | No healthy ECS tasks — check GitLab pipeline logs and ECS service events |
| Playground shows “offline” but site loads | API down or unreachable from Amplify; always verify **direct API** `/health` first |
| Smoke fails only on fishing endpoints | Slow USGS/NWS upstream or timeout; check ECS task logs |

Unit tests validate code correctness; they **do not** detect ECS/ALB outages — use post-deploy smoke.

**Agent context:** see `AGENTS.md` in this repo and `../portfolio-web/AGENTS.md` for full-stack notes.

## Local full-stack workspace

Open `portfolio.code-workspace` in Cursor to run API + `portfolio-web` together (see `.vscode/` and web repo `AGENTS.md`).

