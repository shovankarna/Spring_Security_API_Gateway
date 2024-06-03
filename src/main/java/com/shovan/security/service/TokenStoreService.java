package com.shovan.security.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenStoreService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String TOKEN_PREFIX = "token:";

    public void storeToken(String username, String token, long expirationTime) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + username, token, expirationTime, TimeUnit.MILLISECONDS);
    }

    public String getToken(String username) {
        return (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + username);
    }

    public void deleteToken(String username) {
        redisTemplate.delete(TOKEN_PREFIX + username);
    }

    public boolean isTokenPresent(String username) {
        return redisTemplate.hasKey(TOKEN_PREFIX + username);
    }

    public boolean isTokenValid(String username, String receivedToken) {
        String storedToken = getToken(username);
        return storedToken != null && storedToken.equals(receivedToken);
    }

}
