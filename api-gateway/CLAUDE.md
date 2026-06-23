# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ApiGatewayApplicationTests

# Build without tests
./mvnw clean package -DskipTests
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Architecture

This is the **API Gateway** service for the ecommerce platform, built with **Spring Cloud Gateway** (reactive/WebFlux — not servlet-based). It runs on port 8080 and proxies requests to five downstream services:

| Route prefix       | Downstream service          | Default URL              |
|--------------------|-----------------------------|--------------------------|
| `/users/**`        | user-service                | `http://localhost:8081`  |
| `/products/**`     | product-service             | `http://localhost:8082`  |
| `/orders/**`       | order-service               | `http://localhost:8083`  |
| `/payments/**`     | payment-service             | `http://localhost:8084`  |
| `/notifications/**`| notification-service        | `http://localhost:8085`  |

Service URLs are overridden in production via environment variables (`USER_SERVICE_URL`, `PRODUCT_SERVICE_URL`, etc.).

## JWT Authentication Flow

`JwtAuthFilter` is a `GlobalFilter` with `order=-1` (runs before all other filters). It:

1. Skips validation for public endpoints: `/users/login`, `/users/register`, `/users`
2. Validates the `Authorization: Bearer <token>` header using `JwtUtil`
3. Extracts `email` (subject) and `role` claims from the token
4. Forwards them downstream as `X-UserService-Email` and `X-UserService-Role` request headers

`SecurityConfig` mirrors these public endpoints using WebFlux security (`@EnableWebFluxSecurity`). Both places must stay in sync when adding new public endpoints.

The JWT secret is Base64-decoded from `jwt.secret` (env: `JWT_SECRET`). The default value in `application.yml` is for local development only.
