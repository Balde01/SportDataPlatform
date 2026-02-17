# SportData Platform

## Overview

SportData Platform is a structured backend project designed for **sports data collection** with controlled access for:

- Agents (data collectors)
- Clients (future phase)

The platform is built progressively with a clean and scalable architecture, starting with an Identity/Auth core.

---

## Current Status

- ✅ Phase A – Identity/Auth MVP (implemented and fully unit-tested)
- ⏳ Phase B – Admin / Provisioning (planned)
- ⏳ Phase C – Governance / Audit (planned)
- 🔜 Domain Logic – Sports data collection workflows

---

## Repository Structure

SportDataPlatform/
│
├── apps/
│ └── auth-service/ → Identity/Auth MVP
│
├── docs/
│ └── architecture/
│ └── identity-access/ → Detailed documentation (FR / EN)
│
└── README.md

---

## Architecture Documentation

All architecture documentation is located in:

docs/architecture/identity-access/

Main documents include:

- `overview_EN.md`
- `auth-core_EN.md`
- `authentication_FR.md`
- `admin-provisioning_EN.md`
- `governance-audit_EN.md`

Visual diagrams are available in:

docs/architecture/identity-access/diagrams-visual/

---

## How to Run (Auth Service)

See:

apps/auth-service/README.md

---

## Roadmap (High-Level)

1. Identity/Auth MVP (accounts, invitations, login, refresh tokens)
2. Admin provisioning (agent lifecycle management)
3. Governance/Audit (traceability, security logs)
4. Domain logic for sports data collection

---

## Design Principles

- Clean architecture separation (domain / repository / service / security)
- Test-driven development mindset
- Incremental phase-based growth
- Scalability-ready structure

---

## License

This project is licensed under the MIT License.
See the [LICENSE](LICENSE) file for details.