# Diagram B – Administration / Provisioning (Phase 2)

## 1. Goal
This diagram describes **administrative management of AGENT accounts**.
It operates **on top of** the authentication core (Diagram A).

Its purpose is to allow ADMIN users to:
- provision AGENT accounts
- send invitations
- reissue expired invitations
- manage roles and statuses

---

## 2. Scope

### Included
- Agent provisioning
- Invitation reissue
- Role management
- Account enable / disable

### Excluded
- Authentication
- CLIENT registration
- Audit and supervision
- Sports data collection

---

## 3. Core Service

### UserManagementService
Central service that:
- checks permissions (via AuthorizationService)
- orchestrates admin actions
- delegates activation to invitations

No authentication logic exists here.

---

## 4. Security & Authorization

### AuthorizationService
Responsible for:
- ensuring ADMIN privileges
- preventing unauthorized actions

Access rules are centralized.

---

## 5. Dependency on Diagram A
This diagram depends on:
- User
- UserRepository
- InvitationService

It **uses** invitation logic, it does not reimplement it.

---

## 6. Design Philosophy
- Clear separation between identity and administration
- No impact on authentication core
- Natural extension of the MVP
