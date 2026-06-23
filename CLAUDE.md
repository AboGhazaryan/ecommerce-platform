# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Each service is an independent Maven project. Run commands from within the service directory. On Windows use `mvnw.cmd` instead of `./mvnw`.

```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Build without tests
./mvnw clean package -DskipTests
```

**Frontend** (Angular, runs on port 4200):
```bash
cd frontend
npm install
npm start       # ng serve
npm test        # vitest
npm run build
```

## Service Map

| Service              | Port | Database (host port)           |
|----------------------|------|--------------------------------|
| api-gateway          | 8080 | —                              |
| user-service         | 8081 | PostgreSQL 5432 / ecommerce_user |
| product-service      | 8082 | PostgreSQL 5433 / ecommerce_product |
| order-service        | 8083 | PostgreSQL 5434 / ecommerce_orders |
| payment-service      | 8084 | PostgreSQL 5435 / ecommerce_payments |
| notification-service | 8085 | PostgreSQL 5436 / ecommerce_notifications |

Each service has its own `docker-compose.yml` for spinning up its database. `payment-service/docker-compose.yml` also starts Kafka (KRaft mode, port 9092).

Database credentials default to `postgres`/`postgres` (overridden via `USER_NAME` / `USER_PASSWORD` env vars). The user-service compose uses password `postres` (typo in the config).

## Architecture

### Request Flow

```
Angular (4200) → API Gateway (8080) → [user/product/order/payment/notification]-service
```

The **api-gateway** is built on Spring Cloud Gateway (reactive/WebFlux). Its `JwtAuthFilter` (order=-1) validates JWT tokens and forwards `X-UserService-Email` and `X-UserService-Role` headers to all downstream services. Public endpoints (`/users/login`, `/users/register`, `/users` GET) bypass JWT validation. CORS is configured for `http://localhost:4200`.

Downstream services **do not re-validate JWT** — they trust the gateway. The order-service, for example, disables all Spring Security (`anyRequest().permitAll()`).

### Async Order Pipeline (Kafka)

Order creation triggers a chain of Kafka events:

1. **order-service** saves the order, then publishes to topic `order-created`  
2. **payment-service** consumes `order-created` → saves a `COMPLETED` payment → publishes to `payment-completed`  
3. **notification-service** consumes `payment-completed` → saves a notification record → sends an email via Gmail SMTP

The `OrderEvent` and `PaymentEvent` classes are duplicated in each consuming service (no shared library).

### Synchronous Inter-service Calls (Feign)

**order-service** calls two downstream services directly (not through the gateway) using OpenFeign:
- `ProductClient` → `${product.service.url}/products/{id}` to fetch price at order creation time
- `UserClient` → `${user.service.url}/users/{id}` to fetch user email for the Kafka event

### JWT

The same secret is shared between `user-service` (which issues tokens) and `api-gateway` (which validates them). The secret is configured via `jwt.secret` / `JWT_SECRET` env var. The default value in both `application.yml` files must match.

### Database Migrations

All services use **Liquibase** with `ddl-auto: none`. Schema changes belong in `src/main/resources/db/changelog/` XML files, referenced from `changelog-master.xml` (payment-service uses `master-changlog.xml` — note the typo).

### Code Generation

All backend services use **Lombok + MapStruct**. The annotation processor order in `pom.xml` matters: Lombok must come before MapStruct, with `lombok-mapstruct-binding` listed third. MapStruct interfaces live in `mapper/` packages.

### Spring Boot Version Inconsistency

Services are on different Spring Boot versions:
- `api-gateway`: 3.3.13 (Spring Cloud 2023.0.5)
- `user-service`, `product-service`, `order-service`: 4.0.x
- `payment-service`, `notification-service`: 4.1.0

The api-gateway uses an older version because Spring Cloud Gateway compatibility with Boot 4.x was not yet established when it was written.
