package com.ecommerce.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiValidationError {
    private String message;
    private List<FieldErrorDto> fieldErrors;
}
