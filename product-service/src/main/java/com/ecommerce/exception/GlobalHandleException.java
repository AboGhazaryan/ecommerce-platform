package com.ecommerce.exception;

import com.ecommerce.exception.dto.ApiExceptionResponse;
import com.ecommerce.exception.dto.ApiValidationError;
import com.ecommerce.exception.dto.FieldErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalHandleException {

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

    @ExceptionHandler(ProductNotFountException.class)
    public ResponseEntity<ApiExceptionResponse> handleProductNotFoundException(ProductNotFountException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(ToManyImagesException.class)
    public ResponseEntity<ApiExceptionResponse> handleToManyImagesException(ToManyImagesException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleImageNotFoundException(ImageNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidProductImageException.class)
    public  ResponseEntity<ApiExceptionResponse> handleInvalidProductImageException(InvalidProductImageException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(ProductImageEmptyException.class)
    public ResponseEntity<ApiExceptionResponse> handleProductImageEmptyException(ProductImageEmptyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiExceptionResponse> handleFileStorageException(FileStorageException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiExceptionResponse> handleUnauthorizedActionException(UnauthorizedActionException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiExceptionResponse> handleInsufficientStockException(InsufficientStockException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionResponse(e.getMessage()));
    }


}
