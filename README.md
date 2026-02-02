# SportData Platform

## Overview
SportData is a platform project for **sports data collection** with controlled access for **agents** and later **clients**.
This repository is structured to stay **clean, scalable, and easy to understand**.

## Current status
- ✅ Phase A (Identity/Auth MVP): in progress
- ⏳ Phase B (Admin/Provisioning): planned
- ⏳ Phase C (Governance/Audit): planned

## Repository structure
- `apps/` → runnable applications/modules
  - `apps/auth-service/` → Identity/Auth MVP (registration, invitations, login, tokens)
- `docs/` → architecture and diagrams (FR/EN)

## Documentation
- Architecture overview: `docs/architecture/ARCHITECTURE_FINAL_OVERVIEW_EN.md`
- Detailed docs per phase:
  - Phase A: `docs/architecture/ARCHITECTURE_EN.md`
  - Phase B: `docs/architecture/ARCHITECTURE_DIAGRAM_B_EN.md`
  - Phase C: `docs/architecture/ARCHITECTURE_DIAGRAM_C_EN.md`

## How to run (Auth Service)
See: `apps/auth-service/README.md`

## Roadmap (high-level)
1. Identity/Auth MVP (accounts, invitations, login)
2. Admin provisioning (accept agents, roles, status)
3. Governance/audit (admin traceability)
4. Domain logic: data collection (agents workflow), then client features

## License
TBD
