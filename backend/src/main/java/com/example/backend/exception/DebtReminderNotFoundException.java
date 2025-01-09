package com.example.backend.exception;

public class DebtReminderNotFoundException extends Exception {
    public DebtReminderNotFoundException(String message) {
        super(message);
    }
}