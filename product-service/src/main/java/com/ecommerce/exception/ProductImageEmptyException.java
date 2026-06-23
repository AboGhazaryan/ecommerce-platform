package com.ecommerce.exception;

public class ProductImageEmptyException extends RuntimeException {
    public ProductImageEmptyException(String message) {
        super(message);
    }
}
