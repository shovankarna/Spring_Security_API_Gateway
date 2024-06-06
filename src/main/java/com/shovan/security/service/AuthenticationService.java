package com.shovan.security.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shovan.security.dto.AuthenticationRequest;
import com.shovan.security.dto.AuthenticationResponse;
import com.shovan.security.dto.EnableDisableRequest;
import com.shovan.security.dto.PasswordResetRequest;
import com.shovan.security.dto.RegisterRequest;
import com.shovan.security.entity.Role;
import com.shovan.security.entity.User;
import com.shovan.security.error.ApiException;
import com.shovan.security.repository.RoleRepository;
import com.shovan.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UserRepository userRepository;

        private final RoleRepository roleRepository;

        private final PasswordEncoder passwordEncoder;

        private final JwtService jwtService;

        private final AuthenticationManager authenticationManager;

        private final TokenStoreService tokenStoreService;

        private static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 24;

        public AuthenticationResponse register(RegisterRequest request) {

                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new ApiException(HttpStatus.CONFLICT, "Email already in use");
                }

                User user = new User();
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                Role userRole = roleRepository.findByName("ROLE_USER")
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User role not found"));
                user.getRoles().add(userRole);

                userRepository.save(user);
                String token = jwtService.generateToken(user);
                tokenStoreService.storeToken(user.getEmail(), token, TOKEN_EXPIRATION_TIME);
                return AuthenticationResponse.builder().token(token).build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {

                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(request.getEmail(),
                                                        request.getPassword()));
                } catch (BadCredentialsException e) {
                        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
                }

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

                if (tokenStoreService.isTokenPresent(user.getEmail())) {
                        String token = tokenStoreService.getToken(user.getEmail());
                        return AuthenticationResponse.builder().token(token).build();
                }

                String jwtToken = jwtService.generateToken(user);
                tokenStoreService.storeToken(user.getEmail(), jwtToken, TOKEN_EXPIRATION_TIME);

                return AuthenticationResponse.builder().token(jwtToken).build();
        }

        public void logout(String email) {
                if (!userRepository.findByEmail(email).isPresent()) {
                        throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
                }
                tokenStoreService.deleteToken(email);
        }

        public AuthenticationResponse refreshToken(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

                String jwtToken = jwtService.generateToken(user);
                tokenStoreService.storeToken(email, jwtToken, TOKEN_EXPIRATION_TIME);

                return AuthenticationResponse.builder().token(jwtToken).build();
        }

        public void resetPassword(PasswordResetRequest request) {
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
        }

        public void enableDisableAccount(EnableDisableRequest request) {
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
                user.setEnabled(request.isEnabled());
                userRepository.save(user);
        }

        public void assignRoleToUser(String email, String roleName) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

                Role role = roleRepository.findByName(roleName)
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Role not found"));

                user.getRoles().add(role);
                userRepository.save(user);
        }

        public void deleteUser(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
                userRepository.delete(user);
        }

}