# Architecture – Authentication Platform (MVP)

## 1. System Goal
This architecture represents the **authentication and identity core** of the platform.
It focuses on:
- CLIENT account creation
- Controlled AGENT provisioning
- Password management
- Secure login
- Role and status handling

This is a **functional MVP**, designed for future growth.

---

## 2. Diagram Scope
### Included
- CLIENT registration
- AGENT provisioning by invitation
- AGENT activation via password setup
- Invitation expiration (7 days)
- Token-based authentication
- Roles and statuses

### Explicitly Excluded
- Administrative audit
- Advanced governance
- Sports data collection
- Match business logic
- User interface

---

## 3. Overall Architecture
Layered architecture:

DTO → Services → Repositories → Model  
                ↓  
             Util / Security

Each layer has a single responsibility.

---

## 4. Domain Model
### User
- id (UUID)
- email
- passwordHash
- roles (Set<Role>)
- status
- failedAttempts
- createdAt
- lastLoginAt

The model is intentionally simple.

### InvitationToken
- single use
- time-limited (7 days)
- activation or password reset

---

## 5. AGENT Workflow
1. Agent passes training (external system)
2. AGENT account created as DISABLED
3. Invitation sent (valid for 7 days)
4. Agent sets password
5. Account activated
6. Invitation expires → reissue required

---

## 6. Time Management
A dedicated `Clock` class is used as the single time source to ensure:
- consistency
- testability
- reliable business rules

---

## 7. Design Philosophy
- strict separation of concerns
- intentionally limited MVP scope
- extensibility without over-engineering
