package com.shovan.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shovan.security.dto.EnableDisableRequest;
import com.shovan.security.dto.RoleAssignmentRequest;
import com.shovan.security.entity.Role;
import com.shovan.security.service.AuthenticationService;
import com.shovan.security.service.RoleService;

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
    public ResponseEntity<Role> createRole(@RequestBody String roleName) {

        Role role = roleService.createRole(roleName);
        return ResponseEntity.ok(role);
    }

    @PostMapping("/assign-role")
    public ResponseEntity<Void> assignRole(@RequestBody RoleAssignmentRequest request) {

        authenticationService.assignRoleToUser(request.getEmail(), request.getRoleName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/enable-disable")
    public ResponseEntity<Void> enableDisableAccound(@RequestBody EnableDisableRequest request) {
        authenticationService.enableDisableAccount(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-user")
    public ResponseEntity<Void> deleteUser(@RequestBody String email) {
        authenticationService.deleteUser(email);
        return ResponseEntity.ok().build();
    }

}
