# SportData Platform
![Java CI](https://github.com/Balde01/SportDataPlatform/actions/workflows/build.yml/badge.svg?branch=develop)

## Overview

SportData Platform is a platform for collecting, structuring and managing sports data related to competitions, participants and performance through secure services and user-facing applications.

It is designed to support the lifecycle of sports data, from collection and organization to visualization and data-driven applications.

---

## Current Status

The project is currently focused on completing the technical foundation required before introducing the sports data collection domain.

### Phase A – Identity/Auth Core

In progress

Implemented:

- Account registration and invitation flows
- Authentication and account status handling
- PostgreSQL persistence with JDBC
- Flyway-managed database schema
- Unit and PostgreSQL integration tests
- GitHub Actions CI with PostgreSQL and Flyway

Remaining:

- HTTP / REST API layer
- Persistent refresh tokens
- Production-ready JWT
- Authentication concurrency and transaction hardening

### Phase B – Admin / Provisioning

Planned

### Phase C – Governance / Audit

Planned

### Sports Data Domain

Planned

### Web Application

Planned

The web application will provide user-facing interfaces for sports data,
administration and other platform workflows.

---

## Repository Structure

```text
SportDataPlatform/
├── .github/
│   └── workflows/
│       └── build.yml
│
├── apps/
│   └── auth-service/
│       ├── app/                   → Java source code and tests
│       ├── config/                → Development tooling configuration
│       ├── db/
│       │   └── migration/         → Flyway database migrations
│       ├── docker/                → Local PostgreSQL environment
│       └── README.md
│
├── docs/
│   ├── architecture/
│   │   └── identity-access/       → Identity and access documentation
│   ├── diagrams-visual/
│   │   └── identity-access/       → Exported architecture diagrams
│   └── drawio/                    → Editable diagram sources
│
└── README.md
```
---

## Architecture Documentation

All architecture documentation is located in:

[`docs/architecture/identity-access/`](docs/architecture/identity-access/)

Main documents include:

- `overview_EN.md`
- `auth-core_EN.md`
- `authentication_FR.md`
- `admin-provisioning_EN.md`
- `governance-audit_EN.md`

Visual diagrams are available in:

[`docs/diagrams-visual/identity-access/`](docs/diagrams-visual/identity-access/)

---

## How to Run (Auth Service)

See:

[`apps/auth-service/README.md`](apps/auth-service/README.md)

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

## Roadmap (High-Level)

1. Complete the Identity/Auth foundation and expose it through an HTTP / REST API
2. Introduce the web application
3. Admin provisioning
4. Governance and audit
5. Sports data collection and management features

---

## Continuous Integration

GitHub Actions validates the Auth Service on pull requests and pushes to
`develop` and `main`.

The pipeline:

1. Builds the Auth Service, checks source formatting and runs unit tests
2. Starts PostgreSQL 16 with Docker Compose
3. Applies Flyway migrations
4. Runs PostgreSQL integration tests
5. Cleans up the Docker environment

---

## Design Principles

- Clean Architecture principles with clear separation of domain, application and infrastructure concerns
- Test-driven development mindset
- Incremental phase-based growth
- Explicit abstractions between business logic and persistence

---

## License

This project is licensed under the MIT License.
See the [LICENSE](LICENSE) file for details.