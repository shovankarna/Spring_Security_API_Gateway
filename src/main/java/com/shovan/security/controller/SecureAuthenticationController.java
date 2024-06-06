package com.shovan.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shovan.security.dto.AuthenticationResponse;
import com.shovan.security.dto.PasswordResetRequest;
import com.shovan.security.error.ApiException;
import com.shovan.security.service.AuthenticationService;
import com.shovan.security.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/secure/auth")
@RequiredArgsConstructor
public class SecureAuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            authenticationService.resetPassword(request);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK, "Password reset successfully", null);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody String email) {
        try {
            authenticationService.logout(email);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK, "Logged out successfully", null);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@RequestBody String email) {
        try {
            AuthenticationResponse response = authenticationService.refreshToken(email);
            ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>(HttpStatus.OK,
                    "Token refreshed successfully", response);
            return ResponseEntity.ok(apiResponse);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }
}