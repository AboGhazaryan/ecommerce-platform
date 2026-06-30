package com.ecommerce.feignClient;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignClientInterceptor implements RequestInterceptor {
    private final HttpServletRequest request;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String role = request.getHeader("X-UserService-Role");
        String userId = request.getHeader("X-UserService-UserId");

        if(role != null) requestTemplate.header("X-UserService-Role", role);
        if(userId != null) requestTemplate.header("X-UserService-UserId", userId);
    }
}
