package com.example.backend.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

    private final EmailService emailService;

    public OTPService(EmailService emailService) {
        this.emailService = emailService;
    }

    public String generateAndSendOTP(String email) {
        System.out.println("generateAndSendOTP");

        SecureRandom secureRandom = new SecureRandom();
        int otp = secureRandom.nextInt(900000) + 100000;
        String otpString = String.valueOf(otp);

        otpStorage.put(email, otpString);

        System.out.println("start send email");

        emailService.sendEmail(email, "Your OTP Code", "Your OTP code is: " + otp);

        return otpString;
    }

    public boolean verifyOTP(String otp) {
        return otpStorage.containsValue(otp);
    }
}

