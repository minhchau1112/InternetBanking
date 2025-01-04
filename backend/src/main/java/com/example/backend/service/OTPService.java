package com.example.backend.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OTPService {

    private final RedisTemplate<String, String> redisTemplate;

    public OTPService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOtp(String email, String otp, long ttl) {
        String key = "OTP:" + email;
        redisTemplate.opsForValue().set(key, otp, ttl, TimeUnit.MINUTES);
    }

    public String getOtp(String email) {
        String key = "OTP:" + email;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteOtp(String email) {
        String key = "OTP:" + email;
        redisTemplate.delete(key);
    }
}