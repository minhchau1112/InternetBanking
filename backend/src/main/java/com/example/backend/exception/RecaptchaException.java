package com.example.backend.exception;


public class RecaptchaException extends RuntimeException {

    public RecaptchaException(String message) {
        super(message);
    }

    public RecaptchaException(String message, Throwable cause) {
        super(message, cause);
    }
}
