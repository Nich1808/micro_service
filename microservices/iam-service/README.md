# iam-service

Identity and Access Management for the ISTAD ITE platform, built on **Spring Authorization
Server** (OAuth 2.1 / OpenID Connect 1.0). Users, roles and permissions are the core domain;
Spring Authorization Server is only an adapter around them.

- Java 25 · Spring Boot 4.1.1 · Spring Security 7.1.1 · PostgreSQL · Liquibase · JTE
- Hexagonal architecture: the domain and application modules contain no Spring, JPA or web code.

---

## 1. Modules

```
iam-service/
├── iam-domain/
│   ├── iam-domain-core/            User, Role, Permission, Identity + assignment records
│   └── iam-application-service/    use cases, commands, results, output ports, services
├── iam-persistence/                JPA entities, repositories, port adapters, Liquibase
├── iam-infrastructure/             Spring Authorization Server adapter, tokens, social login
├── iam-rest-api/                   REST controllers, DTOs, API security, ProblemDetail
├── iam-web/                        JTE pages and en/km message bundles
└── iam-main/                       single Spring Boot entry point + bean wiring
```

Dependency rules (enforced by the module graph):

| Module | Depends on | Must not contain |
| --- | --- | --- |
| `iam-domain-core` | — | Spring, JPA, web |
| `iam-application-service` | `iam-domain-core` | Spring AS, JPA, web |
| `iam-persistence` | `iam-application-service` | controllers |
| `iam-infrastructure` | `iam-application-service` | controllers, JPA entities |
| `iam-rest-api` | `iam-application-service` | persistence, SAS wiring |
| `iam-web` | `iam-application-service` | persistence, SAS wiring |
| `iam-main` | all | domain logic |

`iam-main` is the only `@SpringBootApplication`; the other modules publish plain
libraries (`bootJar` disabled) and are assembled into one deployable application.

---

## 2. Domain model

```
users ──< user_roles >── roles ──< role_permissions >── permissions
  │                                                         │
  └────────< user_permissions >─────────────────────────────┘
users ──< identities   (linked social accounts)

assignment tables (user_roles, role_permissions, user_permissions)
  id · assigned_at · assigned_by · expires_at · revoked_at · revoked_by · reason
```

Rules implemented in `iam-domain-core`:

- **Authorities**: `ROLE_<name>` for roles and `resource:action` for permissions.
- **Effective permissions** = active role permissions **+** direct `GRANT` **−** direct `DENY`.
- Assignments are **revoked, never deleted** (`revoke` requires an actor, a timestamp and an
  optional reason; revocation cannot precede assignment).
- Only active, non-deleted roles/permissions can be assigned.
- System roles and permissions cannot be modified or deleted.
- One identity per provider per user; a user cannot unlink their last sign-in method.
- Soft delete, auditing (`created_*`, `updated_*`) and optimistic locking (`version`) on
  every aggregate.

---

## 3. Use cases

One use case = one class in `iam-application-service/service/**`, each implementing a
single interface from `usecase/**`.

| Area | Use cases |
| --- | --- |
| User | `RegisterUser`, `CreateUser`, `GetUser`, `ListUsers`, `UpdateUserProfile`, `ChangePassword`, `ResetUserPassword`, `EnableUser`, `DisableUser`, `LockUser`, `UnlockUser`, `DeleteUser`, `AssignRolesToUser`, `RevokeRoleFromUser`, `GrantPermissionsToUser`, `RevokePermissionFromUser`, `GetUserAuthorities`, `LoadUserForAuthentication`, `RecordLoginAttempt` |
| Role | `CreateRole`, `GetRole`, `ListRoles`, `UpdateRole`, `ActivateRole`, `DeactivateRole`, `DeleteRole`, `AssignPermissionsToRole`, `RevokePermissionFromRole` |
| Permission | `CreatePermission`, `GetPermission`, `ListPermissions`, `UpdatePermission`, `ActivatePermission`, `DeactivatePermission`, `DeletePermission` |
| Identity | `FindOrProvisionFederatedUser`, `LinkIdentity`, `UnlinkIdentity`, `ListUserIdentities` |

Output ports (`port/out`): `UserRepository`, `RoleRepository`, `PermissionRepository`,
`UserRoleRepository`, `RolePermissionRepository`, `UserPermissionRepository`,
`IdentityRepository`, `PasswordHasher`, `CurrentActorProvider`, `ClientRegistryQuery`,
`AuthorizedApplicationQuery`.
`PasswordHasher` and `CurrentActorProvider` are implemented in `iam-infrastructure`; the rest
in `iam-persistence`.

---

## 4. REST API

Base path `/api`. A Bearer access token is required on every endpoint except
`POST /api/auth/register`. Lists accept `page`, `size` and `sort` (`field,asc|desc`).
All errors are RFC 9457 `application/problem+json`.

### Users

| Method | Endpoint | Use case | Authority |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | `RegisterUserUseCase` | public |
| POST | `/api/users` | `CreateUserUseCase` | `user:create` |
| GET | `/api/users` | `ListUsersUseCase` | `user:read` |
| GET | `/api/users/{uuid}` | `GetUserUseCase` | `user:read` |
| PATCH | `/api/users/{uuid}` | `UpdateUserProfileUseCase` | `user:update` |
| DELETE | `/api/users/{uuid}` | `DeleteUserUseCase` | `user:delete` |
| POST | `/api/users/{uuid}/enable` | `EnableUserUseCase` | `user:update` |
| POST | `/api/users/{uuid}/disable` | `DisableUserUseCase` | `user:update` |
| POST | `/api/users/{uuid}/lock` | `LockUserUseCase` | `user:update` |
| POST | `/api/users/{uuid}/unlock` | `UnlockUserUseCase` | `user:update` |
| PUT | `/api/users/{uuid}/password` | `ResetUserPasswordUseCase` | `user:update` |
| GET | `/api/users/{uuid}/authorities` | `GetUserAuthoritiesUseCase` | `user:read` |
| POST | `/api/users/{uuid}/roles` | `AssignRolesToUserUseCase` | `user:assign` |
| DELETE | `/api/users/{uuid}/roles/{roleUuid}` | `RevokeRoleFromUserUseCase` | `user:assign` |
| POST | `/api/users/{uuid}/permissions` | `GrantPermissionsToUserUseCase` | `user:assign` |
| DELETE | `/api/users/{uuid}/permissions/{permissionUuid}` | `RevokePermissionFromUserUseCase` | `user:assign` |
| GET | `/api/users/{uuid}/identities` | `ListUserIdentitiesUseCase` | `user:read` |
| DELETE | `/api/users/{uuid}/identities/{identityUuid}` | `UnlinkIdentityUseCase` | `user:update` |

### Me

| Method | Endpoint | Use case | Authority |
| --- | --- | --- | --- |
| GET | `/api/me` | `GetUserUseCase` | authenticated |
| PATCH | `/api/me` | `UpdateUserProfileUseCase` | authenticated |
| PUT | `/api/me/password` | `ChangePasswordUseCase` | authenticated |
| GET | `/api/me/identities` | `ListUserIdentitiesUseCase` | authenticated |
| DELETE | `/api/me/identities/{identityUuid}` | `UnlinkIdentityUseCase` | authenticated |

### Roles

`POST /api/roles` (`role:create`), `GET /api/roles` (`role:read`),
`GET|PATCH|DELETE /api/roles/{uuid}` (`role:read|update|delete`),
`POST /api/roles/{uuid}/activate|deactivate` (`role:update`),
`POST /api/roles/{uuid}/permissions` and
`DELETE /api/roles/{uuid}/permissions/{permissionUuid}` (`role:assign`).

### Permissions

`POST /api/permissions` (`permission:create`), `GET /api/permissions` (`permission:read`),
`GET|PATCH|DELETE /api/permissions/{uuid}` (`permission:read|update|delete`),
`POST /api/permissions/{uuid}/activate|deactivate` (`permission:update`).

### Error shape

```json
{
  "type": "https://iam.istad.co/problems/validation-error",
  "title": "Validation failed",
  "status": 400,
  "errors": [{ "field": "email", "message": "must be a well-formed email address" }]
}
```

---

## 5. Authorization server

Issuer: `http://localhost:9026` (configurable with `IAM_ISSUER`).

| Component | Implementation |
| --- | --- |
| `RegisteredClientRepository` | `JdbcRegisteredClientRepository` |
| `OAuth2AuthorizationService` | `JdbcOAuth2AuthorizationService` |
| `OAuth2AuthorizationConsentService` | `JdbcOAuth2AuthorizationConsentService` |
| `UserDetailsService` | adapter → `LoadUserForAuthenticationUseCase` |
| Token customizer | adds roles/permissions and maps OIDC claims |

Seeded clients (`RegisteredClientSeeder`):

| client_id | Purpose |
| --- | --- |
| `oidc-client` | public PKCE client used by the web app (`openid profile email phone address`) |
| `dcr-initial-access` | confidential client with `client.create`, used as the Dynamic Client Registration initial access token (secret from `IAM_DCR_CLIENT_SECRET`) |

### Scopes → claims

| Scope | Claims |
| --- | --- |
| `openid` | `sub` = `users.uuid` |
| `profile` | `name`, `given_name`, `family_name`, `middle_name`, `nickname`, `preferred_username`, `profile`, `picture`, `website`, `gender`, `birthdate`, `zoneinfo`, `locale`, `updated_at` |
| `email` | `email`, `email_verified` |
| `phone` | `phone_number`, `phone_number_verified` |
| `address` | `address` |

Access tokens additionally carry `roles`, `permissions` and the combined `authorities`
array, which is what `/api/**` authorises against.

### Dynamic Client Registration

```http
POST /connect/register
Authorization: Bearer <token with scope client.create>
Content-Type: application/json

{
  "client_name": "ISTAD E-Commerce Web",
  "redirect_uris": ["http://localhost:8081/login/oauth2/code/iam"],
  "grant_types": ["authorization_code", "refresh_token"],
  "response_types": ["code"],
  "scope": "openid profile email",
  "token_endpoint_auth_method": "client_secret_basic"
}
```

Spring Authorization Server 7 rejects any scope during DCR by default. This project installs
a validator that keeps the strict redirect-URI / JWK-set checks but accepts the requested
scopes, so the payload above (which the assignment specifies) is accepted.

### Social login

`google`, `github`, `facebook` and `telegram` are wired through
`FindOrProvisionFederatedUserUseCase`:

1. existing identity for `(provider, provider_subject)` → sign in and update `last_login_at`;
2. otherwise an existing user with the same verified email → link a new identity;
3. otherwise provision a new user (password-less) and link the identity.

Linking a social account from the profile page never switches the signed-in user: the
original authentication is parked in the session and restored by
`LinkAccountSuccessHandler`.

---

## 6. Web pages

| Route | Template | Purpose |
| --- | --- | --- |
| `GET /login` | `login.jte` | form login, social buttons, language switcher |
| `GET,POST /register` | `register.jte` | sign-up with validation errors |
| `GET /oauth2/consent` | `consent.jte` | client name, requested scopes, allow/cancel |
| `GET /` | `index.jte` | profile: claims, roles, permissions |
| `GET,POST /profile/edit` | `profile/edit.jte` | edit profile |
| `GET,POST /profile/password` | `profile/password.jte` | change password |
| `GET /profile/accounts` | `profile/linked-accounts.jte` | link / unlink social accounts |
| `GET /profile/apps` | `profile/apps.jte` | connected apps, revoke consent |

Bundles: `messages.properties` (en) and `messages_km.properties` (km). Switch language with
`?lang=en|km`; the choice is stored in the session. Templates contain no hard-coded text.

JTE is **precompiled during the build** (the `gg.jte.gradle` plugin), so the pages also work
inside the executable jar.

---

## 7. Running locally

### Prerequisites

- JDK 25
- PostgreSQL on `localhost:9543` with database `db_iam`

```bash
# from the repository root
docker network create ite-net          # once
cd deployment/postgres && docker compose up -d
```

`schema` and seed data are created by Liquibase on first start; `db_iam` is created by
`deployment/postgres/init_db/001_create_db.sql`.

### Configuration

Secrets are only read from environment variables (nothing is committed):

| Variable | Default | Purpose |
| --- | --- | --- |
| `IAM_DB_URL` | `jdbc:postgresql://localhost:9543/db_iam` | datasource |
| `DB_USERNAME` | `iteuser` | datasource |
| `DB_PASSWORD` | — | datasource (required) |
| `SERVER_PORT` | `9026` | HTTP port |
| `IAM_ISSUER` | `http://localhost:9026` | token issuer |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | — | Google login |
| `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET` | — | GitHub login |
| `FACEBOOK_CLIENT_ID` / `FACEBOOK_CLIENT_SECRET` | — | Facebook login |
| `TELEGRAM_CLIENT_ID` / `TELEGRAM_CLIENT_SECRET` | — | Telegram login |
| `IAM_DCR_CLIENT_SECRET` | `dcr-secret` | `dcr-initial-access` client secret |
| `IAM_WEB_REDIRECT_URI` | `http://127.0.0.1:8080/login/oauth2/code/oidc-client` | seeded redirect URI |

### Build and run

```bash
# build everything (runs all tests)
./gradlew -p microservices/iam-service build

# run
./gradlew -p microservices/iam-service :iam-main:bootRun

# or the packaged jar
./gradlew -p microservices/iam-service :iam-main:bootJar
java -jar microservices/iam-service/iam-main/build/libs/iam-main-0.0.1-SNAPSHOT.jar
```

Then open <http://localhost:9026/login>.

---

## 8. Tests

```bash
./gradlew -p microservices/iam-service test
```

| Module | Coverage |
| --- | --- |
| `iam-persistence` | Liquibase changelog integrity; use-case behaviour against in-memory ports (permission naming, role assignment, GRANT→DENY replacement, idempotency) |
| `iam-infrastructure` | BCrypt hashing, provider mapping, JWT claim customizer (`sub`, roles, permissions, OIDC per-scope claims, `FACTOR_*` filtering) |
| `iam-rest-api` | request validation, RFC 9457 problem details, response payload mapping, JWT authority conversion |
| `iam-web` | JTE templates render in English and Khmer, consent page scope handling |
| `iam-main` | every use case in the specification resolves from the wiring configuration |

The suite runs without a database.

---

## 9. Postman

Import `postman/iam-service.postman_collection.json`, then set the collection variables:

- `baseUrl` — default `http://localhost:9026`
- `accessToken` — paste an access token obtained from the `OAuth2` folder

The collection covers registration, the whole user/role/permission surface, `/api/me`, and
the OAuth2 helpers (discovery document, client-credentials initial access token, Dynamic
Client Registration).
