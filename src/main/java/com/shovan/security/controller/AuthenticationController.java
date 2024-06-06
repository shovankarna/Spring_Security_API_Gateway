package com.shovan.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shovan.security.dto.AuthenticationRequest;
import com.shovan.security.dto.AuthenticationResponse;
import com.shovan.security.dto.EnableDisableRequest;
import com.shovan.security.dto.PasswordResetRequest;
import com.shovan.security.dto.RegisterRequest;
import com.shovan.security.error.ApiException;
import com.shovan.security.service.AuthenticationService;
import com.shovan.security.util.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthenticationResponse response = authenticationService.register(request);
            ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>(HttpStatus.OK,
                    "User registered successfully", response);
            return ResponseEntity.ok(apiResponse);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>(HttpStatus.OK,
                    "User authenticated successfully", response);
            return ResponseEntity.ok(apiResponse);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }
}
