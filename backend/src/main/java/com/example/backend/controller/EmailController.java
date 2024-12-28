package com.example.backend.controller;

import com.example.backend.service.EmailService;
import com.example.backend.service.OTPService;
import com.example.backend.utils.annotation.APIMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class EmailController {
    private final EmailService emailService;

    private final Random random = new Random();

    private final OTPService otpService;

    public EmailController(EmailService emailService, OTPService otpService) {
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @GetMapping("/email")
    @APIMessage("Send email to client")
    public String sendEmail() {
        String otp = String.format("%06d", random.nextInt(999999));

        otpService.saveOtp("ltbinh21@clc.fitus.edu.vn", otp, 1);

        Map<String, Object> variables = new HashMap<>();
        variables.put("OTP_CODE", otp);
        emailService.sendEmailFromTemplateSync("ltbinh21@clc.fitus.edu.vn", "Reset Your Password", "verify-email", variables);
        return "Successfully sent email";
    }
}
