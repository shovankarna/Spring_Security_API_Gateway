package com.shovan.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shovan.security.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
}
