package com.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email can't be blank")
    @Email(message = "email should be email")
    private String email;
    @NotBlank(message = "password can't be blank")
    @Size(min = 6,message = "Password's min length is 6")
    private String password;
}