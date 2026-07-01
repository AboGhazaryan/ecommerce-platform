# Ecommerce Platform

A microservices-based ecommerce platform with an Angular frontend and Spring Boot backend services.

## Architecture

```
Angular (4200) → API Gateway (8080) → [user/product/order/payment/notification]-service
```

The **api-gateway** (Spring Cloud Gateway, reactive/WebFlux) validates JWT tokens and forwards user identity headers to downstream services, which trust the gateway and do not re-validate JWT themselves.

### Async Order Pipeline (Kafka)

1. **order-service** saves the order, then publishes to topic `order-created`
2. **payment-service** consumes `order-created` → saves a `COMPLETED` payment → publishes to `payment-completed`
3. **notification-service** consumes `payment-completed` → saves a notification record → sends an email via Gmail SMTP

### Synchronous Inter-service Calls (Feign)

**order-service** calls **product-service** and **user-service** directly via OpenFeign (bypassing the gateway) to fetch price and user email at order creation time.

## Services

| Service              | Port | Database (host port)                      |
|----------------------|------|--------------------------------------------|
| api-gateway          | 8080 | —                                          |
| user-service         | 8081 | PostgreSQL 5432 / ecommerce_user           |
| product-service      | 8082 | PostgreSQL 5433 / ecommerce_product        |
| order-service        | 8083 | PostgreSQL 5434 / ecommerce_orders         |
| payment-service      | 8084 | PostgreSQL 5435 / ecommerce_payments       |
| notification-service | 8085 | PostgreSQL 5436 / ecommerce_notifications  |

Each service has its own `docker-compose.yml` for spinning up its database. `payment-service/docker-compose.yml` also starts Kafka (KRaft mode, port 9092).

## Getting Started

### Backend services

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

Start each service's database (and Kafka, for payment-service) via its `docker-compose.yml` before running the service.

### Frontend

Angular app, runs on port 4200:

```bash
cd frontend
npm install
npm start       # ng serve
npm test        # vitest
npm run build
```

## Notes

- Database credentials default to `postgres`/`postgres` (overridden via `USER_NAME` / `USER_PASSWORD` env vars). The user-service compose uses password `postres` (typo in the config).
- Schema changes use Liquibase (`ddl-auto: none`), defined under `src/main/resources/db/changelog/` in each service.
- Services run on different Spring Boot versions: `api-gateway` is on 3.3.13 (Spring Cloud 2023.0.5) for Spring Cloud Gateway compatibility, while the others are on 4.0.x/4.1.0.

See `CLAUDE.md` for more detailed architectural notes.
