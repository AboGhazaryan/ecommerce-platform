package com.ecommerce.service;

import com.ecommerce.model.entity.User;

public interface JwtService {
    String generateToken(User user);
}
