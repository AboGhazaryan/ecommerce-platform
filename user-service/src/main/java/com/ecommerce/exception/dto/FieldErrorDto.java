package com.ecommerce.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldErrorDto {
    private String message;
    private String field;
}
