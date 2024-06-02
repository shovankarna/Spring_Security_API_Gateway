package com.shovan.security.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shovan.security.entity.Token;
import com.shovan.security.entity.User;
import com.shovan.security.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveToken(String token, User user) {
        Token newToken = new Token();
        newToken.setToken(token);
        newToken.setUser(user);
        tokenRepository.save(newToken);
    }

    public Optional<Token> findToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void revokeToken(String token) {
        Token existingToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        existingToken.setRevoked(true);
        tokenRepository.save(existingToken);
    }
}
