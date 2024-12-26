package com.example.backend.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RefreshTokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeRefreshToken(String username, String refreshToken, long expirationSeconds) {
        String key = "refresh_token:" + username;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(expirationSeconds));
    }

    public String getRefreshToken(String username) {
        String key = "refresh_token:" + username;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String username) {
        String key = "refresh_token:" + username;
        redisTemplate.delete(key);
    }
}
