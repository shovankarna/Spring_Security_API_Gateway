package com.shovan.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shovan.security.dto.AuthenticationResponse;
import com.shovan.security.dto.PasswordResetRequest;
import com.shovan.security.service.AuthenticationService;
import com.shovan.security.util.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
public class DemoController {

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> sayHello() {
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK, "Request successful",
                "Hello from secured Endpoint");
        return ResponseEntity.ok(response);
    }

}
