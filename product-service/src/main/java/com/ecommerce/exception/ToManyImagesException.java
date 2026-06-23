package com.ecommerce.exception;

public class ToManyImagesException extends RuntimeException {
    public ToManyImagesException(String message) {
        super(message);
    }
}
