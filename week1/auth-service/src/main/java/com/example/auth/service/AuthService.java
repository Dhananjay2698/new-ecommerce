package com.example.auth.service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setEnabled(true);
        user.setCreatedDate(LocalDateTime.now());

        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Find user by username
            User user = userRepository.findByUsername(request.getUsername())
                    .orElse(null);

            if (user == null) {
                return new AuthResponse("Invalid username or password");
            }

            // Check if account is enabled
            if (!user.getEnabled()) {
                return new AuthResponse("Account is disabled");
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new AuthResponse("Invalid username or password");
            }

            // Generate JWT token
            String token = jwtService.generateToken(user.getUsername(), user.getRole());
            return new AuthResponse(token, user.getUsername(), user.getRole());

        } catch (Exception e) {
            return new AuthResponse("Invalid username or password");
        }
    }

    public AuthResponse validateToken(String token) {
        if (jwtService.validateToken(token)) {
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);
            return new AuthResponse(token, username, role);
        }
        return new AuthResponse("Invalid token");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.getEnabled())
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(false)
                .authorities(user.getRole())
                .build();
    }
} 