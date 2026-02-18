# Final Architecture – High-Level Overview (SportDataAuth)

## Purpose of this document
This document provides the **final high-level overview** of the SportDataAuth architecture.
It does not replace detailed diagrams (A, B, C), but serves as a **global entry point** to understand the system.

The goal is **clarity**, not technical depth.

---

## Why a split architecture
The system is intentionally divided into **three independent subsystems**:

- **A – Identity / Auth (MVP)**: account and authentication management
- **B – Admin / Provisioning**: administrative management of agents
- **C – Governance / Audit**: supervision and traceability

This split avoids:
- unreadable diagrams
- excessive coupling
- premature complexity

---

## A) Identity / Auth (MVP)
Responsible for:
- CLIENT account creation
- AGENT invitation and activation
- password management
- login and tokens
- user roles and statuses

This is the **minimal functional core** of the system.
All other modules depend on it.

---

## B) Admin / Provisioning
Responsible for:
- accepting/provisioning AGENT accounts
- reissuing invitations
- managing roles
- enabling/disabling accounts

This module:
- does not implement authentication
- uses services from module A
- applies administrative business rules

---

## C) Governance / Audit
Responsible for:
- logging administrative actions
- ensuring accountability
- bootstrapping the SUPER_ADMIN

This module:
- does not affect business logic
- is optional for the MVP
- strengthens long-term security

---

## Simplified global flow
1. A CLIENT registers via A.
2. An AGENT is accepted via B.
3. An invitation is generated via A.
4. The AGENT activates the account.
5. Admin actions can be audited via C.

---