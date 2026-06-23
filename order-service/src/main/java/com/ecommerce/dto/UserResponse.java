package com.ecommerce.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String name;
    private String surname;
    private String email;
}
