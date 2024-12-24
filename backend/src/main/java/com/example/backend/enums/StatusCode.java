package com.example.backend.enums;

import lombok.Getter;

@Getter
public enum StatusCode {
    SUCCESS(200, "Success"),
    NOT_FOUND(400, "Not Found"),
    INVALID(401, "Invalid"),
    FORBIDDEN(403, "Forbidden"),
    FAILED(500, "Failed");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
