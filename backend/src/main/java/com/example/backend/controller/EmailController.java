package com.example.backend.controller;

import com.example.backend.service.EmailService;
import com.example.backend.utils.annotation.APIMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        emailService.sendEmailSync("ltbinh21@clc.fitus.edu.vn", "Verify otp", "123456", false, true);
        return "OK";
    }
}
