package com.example.backend.service;

import com.example.backend.util.EnvUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        System.out.println("sendEmail");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(EnvUtils.get("EMAIL_USERNAME"));
        System.out.println("email sended from: " + message.getFrom());
        mailSender.send(message);
        System.out.println("send email success");
    }
}