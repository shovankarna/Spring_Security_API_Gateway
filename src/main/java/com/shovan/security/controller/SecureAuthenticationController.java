package com.shovan.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shovan.security.dto.AuthenticationResponse;
import com.shovan.security.dto.PasswordResetRequest;
import com.shovan.security.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/secure/auth")
@RequiredArgsConstructor
public class SecureAuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String email) {
        authenticationService.logout(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody String email) {
        return ResponseEntity.ok(authenticationService.refreshToken(email));
    }
}