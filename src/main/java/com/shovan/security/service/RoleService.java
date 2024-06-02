package com.shovan.security.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shovan.security.entity.Role;
import com.shovan.security.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(String roleName) {

        if (roleRepository.findByName(roleName).isPresent()) {
            throw new IllegalArgumentException("Role already exists");
        }
        Role role = new Role();
        role.setName(roleName);

        roleRepository.save(role);
        return role;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
