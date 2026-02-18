# Auth Service – Phase A (Identity / Authentication MVP)

## Overview

The Auth Service is the first core module of SportData Platform.

It manages:

- Client registration
- Agent provisioning via invitation
- Secure login
- JWT access token generation
- Refresh token rotation
- Account status management (ACTIVE / LOCKED / DISABLED)

---

## Tech Stack

- Java 21
- Gradle
- JUnit 5
- BCrypt (password hashing)

---

## Architecture

The service follows a layered structure:
```text
com.sportdataauth
│
├── domain/ → Entities & Value Objects
├── dto/ → Boundary data transfer objects
├── repository/ → Persistence abstractions
├── service/ → Business logic orchestration
├── security/ → JWT, password hashing, refresh tokens
├── policy/ → Credential validation rules
└── util/ → Technical helpers
```
---

## Running the Project

This module currently runs as a test-driven backend component (no REST layer yet).

### Run all tests:

From `apps/auth-service/`:

./gradlew clean test


### Build the module:

./gradlew clean build


Test reports are generated at:

app/build/reports/tests/test/index.html


---

## Test Coverage

The project includes:

- Repository unit tests
- Service layer tests
- Token lifecycle tests
- Expiration handling
- Account status validation

All tests currently pass successfully.

---

## Status

Phase A is implemented and fully tested.

Next step: persistence layer upgrade (PostgreSQL integration).