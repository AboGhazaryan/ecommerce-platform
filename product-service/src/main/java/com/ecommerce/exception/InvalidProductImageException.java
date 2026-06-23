package com.ecommerce.exception;

public class InvalidProductImageException extends RuntimeException {
    public InvalidProductImageException(String message) {
        super(message);
    }
}
