package com.example.backend.controller;

import com.example.backend.service.EmailService;
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

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    @APIMessage("Send email to client")
    public String sendEmail() {
        String otp = String.format("%06d", new Random().nextInt(999999));

        Map<String, Object> variables = new HashMap<>();
        variables.put("OTP_CODE", otp);
        emailService.sendEmailFromTemplateSync("ltbinh21@clc.fitus.edu.vn", "Reset Your Password", "verify-email", variables);
        return "Successfully sent email";
    }
}
