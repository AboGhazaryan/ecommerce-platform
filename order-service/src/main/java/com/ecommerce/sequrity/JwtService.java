package com.ecommerce.sequrity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final HttpServletRequest request;

    public Integer getCurrentUserId() {
        String userId = request.getHeader("X-UserService-UserId");
        if (userId == null) {
            throw new IllegalStateException("X-UserService-UserId header is missing");
        }
        return Integer.parseInt(userId);
    }
}
