package com.mini_project.miniproject.auth.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class AuthRedisRepository {
    private static final String STRING_KEY_PREFIX = "miniproject:jwt:strings:" ;
    private static final String BLACKLIST_KEY_PREFIX = "miniproject:blacklist:tokens:";
    private final ValueOperations<String, String> valueOperations;

    public AuthRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void saveJwtKey(String email, String jwtKey) {
        valueOperations.set(STRING_KEY_PREFIX+email, jwtKey, 1, TimeUnit.HOURS);
    }

    public String getJwtKey(String email) {
        return valueOperations.get(STRING_KEY_PREFIX+email);
    }

    public void deleteJwtKey(String email) {
        valueOperations.getOperations().delete(STRING_KEY_PREFIX+email);
    }

    public void addToBlacklist(String token) {
        valueOperations.set(BLACKLIST_KEY_PREFIX + token, "", 1, TimeUnit.DAYS);
    }

    public boolean isBlacklisted(String token) {
        return valueOperations.get(BLACKLIST_KEY_PREFIX + token) != null;
    }
}