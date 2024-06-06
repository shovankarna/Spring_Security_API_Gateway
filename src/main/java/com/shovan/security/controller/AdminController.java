package com.shovan.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shovan.security.dto.EnableDisableRequest;
import com.shovan.security.dto.RoleAssignmentRequest;
import com.shovan.security.entity.Role;
import com.shovan.security.error.ApiException;
import com.shovan.security.service.AuthenticationService;
import com.shovan.security.service.RoleService;
import com.shovan.security.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationService authenticationService;

    private final RoleService roleService;

    @PostMapping("/create-role")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody String roleName) {
        try {
            Role role = roleService.createRole(roleName);
            ApiResponse<Role> response = new ApiResponse<>(HttpStatus.OK, "Role created successfully", role);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }

    @PostMapping("/assign-role")
    public ResponseEntity<ApiResponse<Void>> assignRole(@RequestBody RoleAssignmentRequest request) {
        try {
            authenticationService.assignRoleToUser(request.getEmail(), request.getRoleName());
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK, "Role assigned successfully", null);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }

    @PostMapping("/enable-disable")
    public ResponseEntity<ApiResponse<Void>> enableDisableAccount(@RequestBody EnableDisableRequest request) {
        try {
            authenticationService.enableDisableAccount(request);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK, "Account status updated successfully", null);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }

    @PostMapping("/delete-user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@RequestBody String email) {
        try {
            authenticationService.deleteUser(email);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK, "User deleted successfully", null);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getStatus(), e.getMessage(), null), e.getStatus());
        }
    }

}
