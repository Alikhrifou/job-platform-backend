package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.request.LoginRequest;
import com.ali.jobmatch.dto.request.RegisterRequest;
import com.ali.jobmatch.dto.response.AuthResponse;
import com.ali.jobmatch.entity.CompanyProfile;
import com.ali.jobmatch.entity.RefreshToken;
import com.ali.jobmatch.entity.Role;
import com.ali.jobmatch.entity.StudentProfile;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.BadRequestException;
import com.ali.jobmatch.repository.CompanyProfileRepository;
import com.ali.jobmatch.repository.StudentProfileRepository;
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
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role role;
        try {
            role = Role.valueOf(registerRequest.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role. Must be STUDENT or COMPANY");
        }

        if (role == Role.ADMIN) {
            throw new BadRequestException("Cannot register as ADMIN via public registration");
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

        if (role == Role.STUDENT) {
            StudentProfile studentProfile = StudentProfile.builder()
                    .user(user)
                    .gpa(0.0)
                    .build();
            studentProfileRepository.save(studentProfile);
        } else if (role == Role.COMPANY) {
            CompanyProfile companyProfile = CompanyProfile.builder()
                    .user(user)
                    .companyName("")
                    .industry("")
                    .employeeCount(0)
                    .build();
            companyProfileRepository.save(companyProfile);
        }

        String token = jwtService.generateTokenFromUsername(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                token,
                refreshToken.getToken(),
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
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                token,
                refreshToken.getToken(),
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getRole().name()
        );
    }
}
