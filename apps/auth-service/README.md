# Auth Service – Identity / Authentication

## Overview

The Auth Service provides the identity and authentication foundation of SportData Platform.

It is responsible for:

- User registration
- Agent provisioning through invitations
- Authentication and account status management
- Access token generation and refresh token handling
- User persistence and role management

---

## Tech Stack

- Java 21
- Gradle
- PostgreSQL 16
- JDBC
- Flyway
- Docker Compose
- JUnit 5
- BCrypt
- GitHub Actions

---

## Architecture

The Auth Service separates domain logic from persistence, security and application-level workflows.

```text
app/src/main/java/com/sportdataauth/
├── domain/              → Entities, value objects, enums and domain rules
├── dto/                 → Application input and output data structures
├── policy/              → Credential and validation policies
├── repository/          → Persistence contracts and implementations
│   └── postgres/        → JDBC / PostgreSQL implementations
├── service/             → Authentication and provisioning use cases
├── security/            → Password and token-related components
└── util/                → Shared technical abstractions
```

Repository interfaces allow application services to operate independently from the persistence implementation. The same contracts can therefore be backed by in-memory implementations for unit tests or PostgreSQL implementations for persistent storage.

---

## Database

The Auth Service uses PostgreSQL for persistent identity and authentication data.

The schema is managed with Flyway migrations located in:

`db/migration/`

The current schema includes:

- `users` – user identity, account status and authentication state
- `user_roles` – role assignments for users
- `invitation_tokens` – invitation and account activation tokens
- `refresh_tokens` – schema reserved for persistent refresh token management

User, role and invitation token persistence are currently backed by PostgreSQL through JDBC.

The `refresh_tokens` table is already part of the schema, but refresh token persistence is not yet wired into the application layer.

The development database runs on PostgreSQL 16 through Docker Compose.

---

## Running the Project

The Auth Service is currently developed and validated as a backend module.
An HTTP / REST API has not been exposed yet.

The commands below use the Gradle wrapper from `apps/auth-service/`.

### Build and unit tests


```bash
./gradlew clean build
```

To run unit tests only:

```bash
./gradlew test
```

Unit tests do not require PostgreSQL.

### Start PostgreSQL

```bash
docker compose -f docker/docker-compose.yml up -d --wait postgres
```

The development database is exposed on:

```text
localhost:5433
```

### Apply Flyway migrations

```bash
docker compose -f docker/docker-compose.yml run --rm flyway
```

Flyway applies the migrations located in:

```text
db/migration/
```

### Run PostgreSQL integration tests

Set the following environment variables:

```text
DB_URL=jdbc:postgresql://localhost:5433/sportdataauth
DB_USER=sportdata
DB_PASSWORD=sportdata
```

Then run:

```bash
./gradlew integrationTest
```

### Stop the development database

```bash
docker compose -f docker/docker-compose.yml down
```

To also remove the local PostgreSQL volume:

```bash
docker compose -f docker/docker-compose.yml down -v
```

> On Windows PowerShell, use `.\gradlew.bat` instead of `./gradlew`.

---

## Testing

Unit tests are located in:

```text
app/src/test/java/
```

They cover domain and service behavior using in-memory dependencies where appropriate.

PostgreSQL integration tests are located in:

```text
app/src/integrationTest/java/
```

They currently validate PostgreSQL user and role persistence against the Flyway-managed schema.

Additional PostgreSQL integration coverage is still planned for invitation tokens. Refresh token persistence is not yet implemented.

---

## Status

The Identity/Auth foundation currently includes:

- Core registration, invitation and authentication workflows
- PostgreSQL persistence for users, roles and invitation tokens
- Flyway-managed database migrations
- Unit and PostgreSQL integration testing
- Continuous integration with GitHub Actions

The service does not expose an HTTP / REST API yet.

Current priorities include persistent refresh token management, production-ready JWT handling, additional persistence integration tests, authentication hardening and the HTTP / REST API layer.
