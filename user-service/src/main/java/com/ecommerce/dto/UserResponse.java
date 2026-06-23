package com.ecommerce.dto;

import com.ecommerce.model.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Integer id;
    private String name;
    private String surname;
    private String email;
    private UserRole role;
    private boolean blocked;
    private LocalDateTime createdAt;
}
