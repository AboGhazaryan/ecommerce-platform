package com.ecommerce.exception;

import com.ecommerce.exception.dto.ApiExceptionResponse;
import com.ecommerce.exception.dto.ApiValidationError;
import com.ecommerce.exception.dto.FieldErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiValidationError> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldErrorDto> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(new ApiValidationError("Validation error", errors));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleOrderNotFoundException(OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiExceptionResponse> handleInsufficientStockException(InsufficientStockException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionResponse(e.getMessage()));
    }
}
