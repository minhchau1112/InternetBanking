package com.example.backend.exception;

import com.example.backend.dto.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = {
            BadCredentialsException.class,
            InvalidException.class
    })
    public ResponseEntity<RestResponse<Object>> handleException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Exception occurs");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            EmailNotFoundException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleEmailNotFound(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("The email was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }
    @ExceptionHandler(value = {
            OTPNotFoundException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleOTPNotFound(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("The OTP was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }
    @ExceptionHandler(value = {
            NoResourceFoundException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("404 Not Found. URL may not exist...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Invalid argument...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
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

    @ExceptionHandler(value = {
            DebtReminderNotFoundException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleDebtReminderNotFound(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("The debt reminder was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(value = {
            AccountNotFoundException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleAccountNotFound(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatus(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("The account was not found");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}