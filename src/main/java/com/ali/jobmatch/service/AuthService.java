package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.request.LoginRequest;
import com.ali.jobmatch.dto.request.RegisterRequest;
import com.ali.jobmatch.dto.response.AuthResponse;
import com.ali.jobmatch.entity.Role;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.BadRequestException;
import com.ali.jobmatch.repository.UserRepository;
import com.ali.jobmatch.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role role;
        try {
            role = Role.valueOf(registerRequest.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role. Must be STUDENT, COMPANY, or ADMIN");
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(role)
                .isActive(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateTokenFromUsername(user.getEmail());

        return new AuthResponse(
                token,
                null,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                role.name()
        );
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        String token = jwtService.generateToken(authentication);

        return new AuthResponse(
                token,
                null,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getRole().name()
        );
    }
}
