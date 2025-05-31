package com.demo.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ShortCodeCollisionException extends RuntimeException{
    public ShortCodeCollisionException(String message) {
        super(message);
    }

    public ShortCodeCollisionException(String message, Throwable cause) {
        super(message, cause);
    }
}
