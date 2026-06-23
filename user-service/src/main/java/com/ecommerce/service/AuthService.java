package com.ecommerce.service;

import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.exception.PasswordMatchException;
import com.ecommerce.exception.UserBlockedException;
import com.ecommerce.exception.UserNotFoundException;
import com.ecommerce.model.entity.User;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isBlocked()) {
            throw new UserBlockedException("Your account has been blocked. Please contact support.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMatchException("Wrong password");
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .build();
    }
}
