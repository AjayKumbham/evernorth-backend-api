package com.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;
    
    public boolean allowRequest(String key, int maxRequests, Duration window) {
        String currentTime = String.valueOf(System.currentTimeMillis());
        String windowStart = String.valueOf(System.currentTimeMillis() - window.toMillis());
        
        // Remove old entries outside the window
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, Double.parseDouble(windowStart));
        
        // Count current requests in window
        long currentCount = redisTemplate.opsForZSet().zCard(key);
        
        if (currentCount >= maxRequests) {
            return false;
        }
        
        // Add current request
        redisTemplate.opsForZSet().add(key, currentTime, Double.parseDouble(currentTime));
        redisTemplate.expire(key, window);
        
        return true;
    }
    
    public void resetRateLimit(String key) {
        redisTemplate.delete(key);
    }
} 