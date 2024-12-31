package com.example.backend.exception;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.enums.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<?> response = new ApiResponse<>(
                false,
                StatusCode.FAILED.getCode(),
                null,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<?> response = new ApiResponse<>(
                false,
                StatusCode.FAILED.getCode(),
                null,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundException(NotFoundException ex) {
        ApiResponse<?> response = new ApiResponse<>(
                false,
                StatusCode.NOT_FOUND.getCode(),
                null,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(value = {
            CustomerNotFoundException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleCustomerNotFound(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("The customer was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(RecaptchaException.class)
    public ResponseEntity<RestResponse<Object>> handleRecaptchaException(RecaptchaException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("reCAPTCHA validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}