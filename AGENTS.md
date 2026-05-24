# AGENTS.md — AI Agent Context for portfolio-api

Read this at the start of sessions on the **Java API**. For the Next.js frontend, see `../portfolio-web/AGENTS.md`.

---

## Summary

Spring Boot **4.x** API for [caseycapozzi.com](https://caseycapozzi.com). Production: **ECS** behind **ALB** at `https://api.caseycapozzi.com`. Frontend on Amplify proxies via Next.js (`/api/status`, `/api/fishing/*`).

---

## Key endpoints

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/health` | ALB/ops health (`UP`) |
| GET | `/api/v1/status` | Status JSON for footer/playground |
| GET | `/api/fishing/brule/status` | Bois Brule bite score (USGS `04025500`, NWS `KSUW`) |
| GET | `/api/fishing/colfax/status` | Red Cedar bite score (USGS `05367500`, NWS `KLUM`) |
| GET | `/swagger-ui.html` | Swagger UI (springdoc 3.x) |

Fishing handlers call **USGS + NWS** via WebClient — responses can take **several seconds**; clients need generous timeouts.

---

## Deploy & CI

GitLab `.gitlab-ci.yml` on `master`:

1. **`unit-tests`** — `./mvnw test` (blocks image build if failing)
2. **`build-image`** — Docker → ECR `portfolio-api:latest`
3. **`deploy-ecs`** — `aws ecs update-service` → wait stable → **`scripts/smoke-api.py`**

Smoke script verifies `/health`, `/api/v1/status`, and both fishing endpoints return `biteScore` 0–100 (with retries).

**Manual smoke:** `python3 scripts/smoke-api.py`

Full deploy/troubleshooting notes: **`README.md`** (Deploy & smoke tests).

---

## Critical conventions

- **springdoc-openapi 3.x** with Spring Boot 4 (e.g. `3.0.3` in `pom.xml`). **Do not use 2.x.**
- **Java 26** (`pom.xml`); Docker sets `--enable-native-access=ALL-UNNAMED` for Netty
- **No datasource** by default (`DataSourceAutoConfiguration` excluded)
- **Lombok**, constructor injection, `@WebMvcTest` + `@MockitoBean` (not deprecated `@MockBean`)

---

## Local dev

```bash
./mvnw spring-boot:run   # port 8080
./mvnw test
```

Open **`portfolio.code-workspace`** (this repo) for combined API + web workspace in Cursor.

---

## Prod troubleshooting

1. `curl -s https://api.caseycapozzi.com/health` — if **503 HTML**, ECS has no healthy tasks (check GitLab pipeline + ECS service `portfolio-api-5b60`).
2. Unit tests do not validate production; only **post-deploy smoke** does.
3. If smoke fails on fishing only, check USGS/NWS reachability from the task (not a code-only issue).
