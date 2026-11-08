# Technical Debt

This document tracks known technical debt in SportDataPlatform.

Its purpose is to make temporary implementations, architectural compromises and hardening work explicit without interrupting unrelated feature development.

The list is not a backlog that must be completed immediately. Technical debt should be addressed when it becomes relevant to the current development phase.

Last full audit: `develop` at `ab80d33`.

---

## Priority Levels

| Priority | Meaning                                                                               |
| -------- | ------------------------------------------------------------------------------------- |
| **P0**   | Must be resolved before the affected functionality can be considered production-ready |
| **P1**   | Important correctness, security or architectural debt                                 |
| **P2**   | Maintainability, consistency or robustness improvement                                |
| **P3**   | Documentation, cleanup or low-risk improvement                                        |

## Status

| Status          | Meaning                                   |
| --------------- | ----------------------------------------- |
| **Open**        | Known debt, not currently being addressed |
| **In progress** | Currently being addressed                 |
| **Deferred**    | Intentionally postponed to a later phase  |

---

# Authentication and Security

### Production-ready access tokens

**Priority:** P0
**Status:** Deferred

`SimpleJwtService` currently generates secure random strings rather than signed JWT access tokens.

Token validation currently verifies only that the supplied value is non-null and non-empty.

Before authentication is exposed as production-ready:

* generate signed JWT access tokens;
* validate signatures;
* validate expiration;
* define required claims;
* define signing-key management;
* integrate authentication with Spring Security.

**Affected area:**

`apps/auth-service/app/src/main/java/com/sportdataauth/security/`

**Target phase:** Spring Boot / Spring Security integration.

---

### Persistent refresh-token lifecycle

**Priority:** P0
**Status:** In progress

Refresh tokens are currently managed through `InMemoryRefreshTokenService`.

The database already contains a `refresh_tokens` table, but persistence is not yet connected to the application.

The final design should:

* store only token hashes;
* support expiration;
* support revocation;
* rotate tokens atomically;
* support multiple sessions per user;
* support revoking one session;
* support revoking all sessions for a user.

The current `rotateRefreshToken(userId)` contract also conflates initial token issuance with actual token rotation and should evolve with the persistent implementation.

**Affected areas:**

* `security/RefreshTokenService.java`
* `security/InMemoryRefreshTokenService.java`
* `db/migration/V1__init.sql`

**Current branch:** `feature/refresh-token-persistence`

---

### Password-reset workflow is incomplete

**Priority:** P0
**Status:** Deferred

`TokenPurpose` contains `PASSWORD_RESET`, but the application does not yet implement a dedicated password-reset workflow.

`InvitationService.acceptInvite()` currently performs activation-oriented behavior that may not be appropriate for password reset, including setting the user status to `ACTIVE`.

Before `PASSWORD_RESET` is exposed through an API, its account-status and security semantics must be explicitly implemented.

**Affected areas:**

* `domain/enums/TokenPurpose.java`
* `service/InvitationService.java`

---

### Invitation lifecycle hardening

**Priority:** P1
**Status:** Open

Invitation issuance and reissuance currently need stronger lifecycle rules.

Known cases include:

* multiple unused invitations for the same user may remain valid simultaneously;
* an existing active AGENT may receive a `FIRST_PASSWORD_SET` invitation that cannot subsequently be accepted;
* reissue behavior is not yet formally defined.

Before Phase B provisioning is exposed, define the rules for active invitations and reissuance.

**Affected areas:**

* `service/InvitationService.java`
* `repository/InvitationTokenRepository.java`
* Phase B provisioning workflow

---

### Agent provisioning email validation is inconsistent

**Priority:** P1
**Status:** Open

CLIENT registration validates the normalized email through `CredentialPolicy`.

AGENT provisioning normalizes the email through `Email.of()` but does not apply the same email acceptance policy.

The platform should have one consistent rule for determining whether an email is acceptable.

**Affected areas:**

* `service/RegisterService.java`
* `service/InvitationService.java`
* `domain/value/Email.java`
* `policy/DefaultCredentialPolicy.java`

---

# Concurrency and Transactions

### Failed-login updates are not concurrency-safe

**Priority:** P1
**Status:** Deferred

Authentication currently follows approximately:

`read User → mutate failedAttempts → update User`

Two concurrent failed login attempts can read the same state and overwrite each other's updates.

This can make lockout behavior unreliable under concurrent requests.

Possible future strategies include:

* atomic SQL updates;
* row locking;
* optimistic locking/versioning;
* Spring-managed transactional persistence.

**Affected areas:**

* `service/AuthService.java`
* `repository/postgres/PostgresUserRepository.java`

---

### User updates are too coarse-grained

**Priority:** P1
**Status:** Open

`UserRepository.update(User)` rewrites general account state and replaces all roles.

Authentication operations normally need to update only authentication-related information such as:

* failed attempts;
* account lock state;
* last login.

Rewriting roles during login creates unnecessary writes and increases the risk of overwriting concurrent administrative changes.

The persistence API should eventually provide safer update boundaries.

**Affected areas:**

* `repository/UserRepository.java`
* `repository/postgres/PostgresUserRepository.java`
* `service/AuthService.java`

---

### Provisioning is not fully transactional

**Priority:** P1
**Status:** Deferred

Creating a disabled AGENT account and creating the corresponding invitation are currently separate operations.

If invitation creation fails after user creation, the account may remain without the invitation expected by the provisioning operation.

The final Phase B provisioning workflow should execute the relevant persistence changes within one transaction.

**Affected area:**

`service/InvitationService.java`

---

### Login and refresh-session persistence need a transaction boundary

**Priority:** P1
**Status:** Open

Once refresh tokens become persistent, login will perform several state changes:

* update successful login state;
* create a refresh session;
* generate the access token.

Database state must remain coherent if refresh-token persistence fails.

The transaction boundary should be finalized during persistent refresh-token integration.

---

### JDBC transaction abstraction needs future hardening

**Priority:** P2
**Status:** Deferred

`JdbcTransactionRunner` currently provides a lightweight transaction abstraction suitable for the current project phase.

Known limitations include:

* no rollback-only state for nested transactions;
* rollback handling is focused on `Exception` / `RuntimeException`;
* transaction semantics are intentionally simpler than a mature transaction framework.

Revisit this abstraction during Spring transaction integration instead of significantly expanding the custom implementation now.

**Affected area:**

`repository/postgres/JdbcTransactionRunner.java`

---

# Domain Model

### InvitationToken lifecycle invariants

**Priority:** P2
**Status:** Open

`InvitationToken` currently validates token expiration relative to creation, but some lifecycle invariants remain implicit.

Known improvements:

* make `usedAt` immutable;
* remove the obsolete `markUsed` comment;
* reject blank `tokenHash`;
* define valid `usedAt` chronology;
* define explicit behavior for null time arguments.

The entity should not be able to represent impossible token states.

**Affected area:**

`domain/entity/InvitationToken.java`

---

### User lifecycle invariants

**Priority:** P1
**Status:** Open

The `User` entity currently permits some states that are inconsistent with database or lifecycle rules.

Examples include:

* an `ACTIVE` user without a password;
* `lastLoginAt` before `createdAt`;
* unrestricted mutation through general setters.

The domain model and PostgreSQL constraints should eventually express the same core invariants.

**Affected area:**

`domain/entity/User.java`

---

### User roles expose mutable collections

**Priority:** P1
**Status:** Open

`User` stores and exposes `Set<Role>` directly.

A mutable set obtained from persistence can therefore be changed outside an explicit domain operation.

Future hardening should use defensive copies or immutable views and define explicit role-management operations.

**Affected area:**

`domain/entity/User.java`

---

### Domain and request exception boundaries are mixed

**Priority:** P2
**Status:** Open

`InvalidRequestException` lives under `domain.exception` and is also used inside domain entities and repository implementations.

For example, `User.recordSuccessfulLogin()` knows about an exception whose semantics refer to a request.

The exception model should eventually distinguish:

* domain invariant violations;
* application/input validation failures;
* persistence/technical failures.

This should be refactored as part of a broader exception-handling pass rather than one class at a time.

---

# Persistence

### PostgreSQL uniqueness errors are mapped too broadly

**Priority:** P1
**Status:** Open

`PostgresUserRepository` maps any PostgreSQL `23505` unique-constraint violation during user insertion to `EmailAlreadyExistsException`.

A duplicate primary key or another future unique constraint could therefore be reported incorrectly as an email conflict.

Constraint-specific error translation should eventually be introduced.

---

### PostgreSQL repository bypasses Clock

**Priority:** P2
**Status:** Open

`PostgresUserRepository.updateUser()` uses `Instant.now()` directly for `updated_at`.

Architecture documentation currently describes `Clock` as the common application time source.

Choose one explicit strategy:

* inject/use `Clock`; or
* treat `updated_at` as infrastructure-managed time and document that exception.

---

### InvitationTokenRepository save contract is ambiguous

**Priority:** P2
**Status:** Open

`save(InvitationToken)` suggests persistence of the complete supplied state.

The PostgreSQL implementation intentionally does not persist `usedAt`.

The contract should explicitly define whether `save()` means:

* create a new unused invitation; or
* persist every entity field.

The current atomic-consumption design suggests that creation of an unused invitation is the intended contract.

---

### JDBC persistence errors are translated inconsistently

**Priority:** P2
**Status:** Open

Several PostgreSQL operations currently surface generic `RuntimeException` wrappers.

A consistent persistence exception strategy should be introduced if application behavior needs to distinguish infrastructure failures.

Avoid introducing a large hierarchy before such distinctions are actually required.

---

### JDBC connection handling is duplicated

**Priority:** P2
**Status:** Deferred

PostgreSQL repositories contain similar logic for:

* obtaining the current transactional connection;
* opening standalone connections;
* wrapping SQL failures.

Adding more repositories will increase duplication.

Do not create a large custom framework solely to remove this duplication.

Prefer either:

* a minimal shared JDBC helper if duplication becomes significant; or
* resolving it naturally when Spring/DataSource infrastructure is introduced.

---

### Direct DriverManager connections are development infrastructure

**Priority:** P1 before production
**Status:** Deferred

`JdbcConnectionFactory` currently creates connections directly through `DriverManager`.

This is sufficient for the current development phase but does not provide connection pooling.

Move to an injected pooled `DataSource` as part of Spring Boot infrastructure.

---

# In-Memory Implementations

### InMemoryUserRepository does not perfectly reproduce persistence semantics

**Priority:** P2
**Status:** Open

The in-memory repository stores and returns the same mutable `User` objects.

Changing a retrieved `User` can therefore modify repository state even without calling `update()`.

PostgreSQL returns reconstructed entities and does not behave this way.

Unit tests using the in-memory repository can therefore hide missing persistence operations.

---

### In-memory and PostgreSQL repository contracts are not fully aligned

**Priority:** P2
**Status:** Open

Some validation, uniqueness and failure behavior differs between the in-memory and PostgreSQL invitation/user repositories.

The implementations do not need identical infrastructure behavior, but externally observable repository contracts should remain aligned.

---

### NoopTransactionRunner cannot simulate rollback

**Priority:** P2
**Status:** Open

Unit tests using `NoopTransactionRunner` cannot reproduce database rollback behavior.

Transaction-sensitive workflows should therefore have PostgreSQL integration coverage.

Do not attempt to build a full transactional in-memory database abstraction solely for unit testing.

---

# Database Integrity

### Database constraints do not mirror all domain invariants

**Priority:** P2
**Status:** Open

The schema currently relies on Java for several rules.

Potential hardening includes:

* `failed_attempts >= 0`;
* allowed values for `UserStatus`;
* allowed values for `Role`;
* allowed values for `TokenPurpose`;
* `expires_at > created_at`;
* valid `used_at` chronology;
* valid `revoked_at` chronology.

Add constraints only after the corresponding domain rules are finalized.

**Affected area:**

`apps/auth-service/db/migration/`

---

# Architecture Boundaries

### AGENT provisioning ownership crosses Phase A and Phase B

**Priority:** P1
**Status:** Deferred

Current Java code contains:

`InvitationService.provisionAgent(...)`

inside the Identity/Auth implementation.

The architecture documentation for Phase B assigns administrative provisioning to `UserManagementService`.

The intended long-term split is:

**Phase A**

* invitation primitives;
* account activation;
* authentication.

**Phase B**

* authorization of administrative actions;
* AGENT provisioning orchestration;
* invitation reissue;
* role/status administration.

Refactor this boundary when Phase B is implemented rather than prematurely moving code now.

**Affected areas:**

* `service/InvitationService.java`
* `docs/plantuml/diagram-a-auth.puml`
* `docs/plantuml/diagram-b-admin.puml`
* Phase A/B architecture documentation

---

# Testing

### Authentication concurrency coverage is missing

**Priority:** P1
**Status:** Deferred

There are currently no PostgreSQL integration tests for concurrent failed login attempts or account lockout.

Add these tests when the concurrency-safe persistence strategy is implemented.

---

### Transaction rollback coverage is incomplete

**Priority:** P1
**Status:** Open

Workflows involving several database mutations need integration tests proving rollback behavior.

Important examples include:

* invitation consumption followed by failed account activation;
* future provisioning transactions;
* future persistent refresh-token login flows.

---

### Invitation workflow edge-case coverage is incomplete

**Priority:** P2
**Status:** Open

Important scenarios still requiring explicit coverage include:

* invalid provisioning email;
* active AGENT receiving `FIRST_PASSWORD_SET`;
* weak password after token consumption;
* invitation reissue;
* multiple active invitations;
* future `PASSWORD_RESET` semantics.

---

### InvitationToken has no dedicated domain test suite

**Priority:** P2
**Status:** Open

`InvitationToken` lifecycle behavior is currently exercised mostly indirectly.

Add focused domain tests when its invariants are hardened.

---

### AuthService successful-login contract test is minimal

**Priority:** P2
**Status:** Open

The successful-login unit test currently focuses mainly on absence of exceptions.

As authentication matures, verify the returned contract explicitly:

* access token;
* refresh token;
* access-token expiration;
* successful-login state changes.

---

### Integration tests are not attached to Gradle check

**Priority:** P3
**Status:** Deferred

`integrationTest` requires a PostgreSQL environment and is deliberately executed separately in CI.

A local `gradlew build` therefore does not execute PostgreSQL integration tests.

This is currently intentional.

Revisit if the project adopts Testcontainers or Spring-managed integration-test infrastructure.

---

# Documentation

### Auth Service README contains stale integration-test status

**Priority:** P3
**Status:** Open

`apps/auth-service/README.md` still states that PostgreSQL integration tests for invitation tokens are planned.

They were implemented and merged in PR #9.

---

### Root README references an incorrect French documentation filename

**Priority:** P3
**Status:** Open

The README references:

`authentication_FR.md`

while the repository currently contains:

`authentification_FR.md`

---

### Authentication documentation overstates current token maturity

**Priority:** P2
**Status:** Open

Detailed authentication documentation uses terms such as secure token authentication while the current access-token implementation remains intentionally provisional.

Documentation should clearly distinguish implemented architecture from production-ready security.

---

### Password-reset documentation is ahead of implementation

**Priority:** P2
**Status:** Open

Architecture documentation describes invitation tokens as supporting password reset, but the dedicated reset workflow is not implemented safely yet.

Mark the capability as planned until implemented.

---

### Clock documentation and implementation are inconsistent

**Priority:** P3
**Status:** Open

Diagram A states that timestamps use `Clock`.

`PostgresUserRepository` currently uses `Instant.now()` for `updated_at`.

Align either implementation or documentation.

---

### Phase A / Phase B provisioning documentation is inconsistent

**Priority:** P2
**Status:** Open

Some Phase A documentation describes AGENT provisioning as belonging to the authentication core, while Phase B documentation assigns it to administration/provisioning.

Align the documentation with the chosen long-term responsibility split.

---

### SUPER_ADMIN concept is not aligned with the role model

**Priority:** P2
**Status:** Deferred

Phase C documentation refers to `SUPER_ADMIN`.

Current `Role` contains only:

* `ADMIN`
* `AGENT`
* `CLIENT`.

Before implementing Governance/Audit, decide whether:

* the root administrator is simply a privileged bootstrapped `ADMIN`; or
* `SUPER_ADMIN` becomes an explicit role.

---

### Documentation index is incomplete

**Priority:** P3
**Status:** Open

`docs/README.md` currently provides only a minimal documentation index.

It should eventually reference:

* architecture documents;
* PlantUML sources;
* exported diagrams;
* Draw.io database sources.

---

# Repository Governance

### Main development branches are not protected

**Priority:** P2
**Status:** Deferred

GitHub currently reports `main` and `develop` as unprotected branches.

When the repository workflow stabilizes, consider requiring:

* pull requests;
* successful CI before merge;
* protection against accidental force pushes or branch deletion.

---

# Cleanup

### Obsolete AuthenticationException declaration

**Priority:** P3
**Status:** Open

`AuthService.login()` declares `javax.security.sasl.AuthenticationException`, but the method currently uses project runtime/domain exceptions.

Remove the obsolete checked-exception declaration.

---

### TokenHasher UTF-8 handling can be simplified

**Priority:** P3
**Status:** Open

`TokenHasher` uses string-based `"UTF-8"` encoding lookup and handles `UnsupportedEncodingException`.

Java guarantees UTF-8 support.

Use `StandardCharsets.UTF_8` when this utility is next touched.

---

### Token exception model contains potentially obsolete exceptions

**Priority:** P3
**Status:** Open

Exceptions such as:

* `TokenExpiredException`
* `TokenAlreadyUsedException`

remain in the domain package while the current atomic invitation-token repository intentionally collapses invalid, expired and already-used tokens into a single unsuccessful consumption result.

Review these exception types when API error semantics are finalized.

Avoid additional database reads solely to distinguish token failure reasons unless the product requires it.

---

# GitHub Issue Policy

This document is the global technical-debt register.

Not every entry should become a GitHub Issue immediately.

Create an Issue only when:

1. the debt is expected to be addressed soon;
2. it requires non-trivial implementation or design work;
3. it benefits from explicit acceptance criteria or discussion;
4. it is independent enough to be completed through a dedicated branch or pull request.

Recommended workflow:

```text
technical-debt.md
        ↓
Select debt to address
        ↓
Create GitHub Issue
        ↓
Create dedicated branch
        ↓
Implement + test
        ↓
Pull Request
        ↓
Close Issue
        ↓
Remove or update debt entry
```

For small cleanup items, a dedicated Issue is unnecessary.

A focused commit or PR is sufficient.

---

# Current Focus

The current development focus is persistent refresh-token management.

Relevant debt being addressed:

* persistent refresh-token lifecycle;
* separation between token issuance and rotation;
* support for multiple concurrent user sessions;
* login transaction implications introduced by refresh-token persistence.

Other debt should not interrupt the current feature unless it directly blocks or invalidates the implementation.
