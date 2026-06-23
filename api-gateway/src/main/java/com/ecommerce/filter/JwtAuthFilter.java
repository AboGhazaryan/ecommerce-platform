package com.ecommerce.filter;

import com.ecommerce.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (isPublicEndpoint(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtUtil.extractAllClaims(token);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        Integer userId = claims.get("userId", Integer.class);

        log.debug("Authenticated user: {}, role: {}", email, role);

        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-UserService-Email", email)
                        .header("X-UserService-Role", role)
                        .header("X-UserService-UserId", String.valueOf(userId))
                        .build())
                .build();

        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path, HttpMethod method) {
        if (path.startsWith("/users/login") || path.startsWith("/users/register")) {
            return true;
        }
        if (path.startsWith("/users") && HttpMethod.GET.equals(method)) {
            return true;
        }
        if (path.startsWith("/products") && HttpMethod.GET.equals(method)) {
            if (path.endsWith("/my-products") || path.endsWith("/pending")) {
                return false;
            }
            return true;
        }
        if (path.startsWith("/images/")) {
            return true;
        }
        return false;
    }
}
