package com.ecommerce.exception;

import com.ecommerce.exception.dto.ApiExceptionResponse;
import com.ecommerce.exception.dto.ApiValidationError;
import com.ecommerce.exception.dto.FieldErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiValidationError> apiValidException(MethodArgumentNotValidException ex) {

       List<FieldErrorDto> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDto(
                        error.getField(),
                        error.getDefaultMessage()
                )).toList();

        return ResponseEntity.badRequest()
                .body(new ApiValidationError("Validation error", errors));
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiExceptionResponse> handleEmailException(EmailAlreadyExistException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(PasswordMatchException.class)
    public ResponseEntity<ApiExceptionResponse> handlePasswordMatchException(PasswordMatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ApiExceptionResponse> handleBlockedException(UserBlockedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiExceptionResponse(e.getMessage()));
    }
}
