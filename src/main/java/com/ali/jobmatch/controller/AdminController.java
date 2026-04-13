package com.ali.jobmatch.controller;

import com.ali.jobmatch.dto.request.RegisterRequest;
import com.ali.jobmatch.entity.Application;
import com.ali.jobmatch.entity.Role;
import com.ali.jobmatch.entity.Skill;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.BadRequestException;
import com.ali.jobmatch.exception.ResourceNotFoundException;
import com.ali.jobmatch.repository.ApplicationRepository;
import com.ali.jobmatch.repository.JobRepository;
import com.ali.jobmatch.repository.UserRepository;
import com.ali.jobmatch.dto.response.ApplicationResponse;
import com.ali.jobmatch.dto.response.JobOfferResponse;
import com.ali.jobmatch.mapper.JobMapper;
import com.ali.jobmatch.service.ApplicationService;
import com.ali.jobmatch.service.JobService;
import com.ali.jobmatch.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management endpoints")
@SecurityRequirement(name = "Bearer Token")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── Users ──────────────────────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "List all users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Strip passwords from the response
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/toggle-active")
    @Operation(summary = "Toggle user active status")
    public ResponseEntity<User> toggleUserActive(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new user (admin can assign any role including ADMIN)")
    public ResponseEntity<User> createUser(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role. Must be STUDENT, COMPANY, or ADMIN");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(role)
                .isActive(true)
                .build();

        userRepository.save(user);
        user.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // ── Jobs ───────────────────────────────────────────────────────

    @GetMapping("/jobs")
    @Operation(summary = "List all jobs (including inactive)")
    public ResponseEntity<List<JobOfferResponse>> getAllJobs() {
        return ResponseEntity.ok(
                jobRepository.findAll().stream()
                        .map(jobMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @DeleteMapping("/jobs/{jobId}")
    @Operation(summary = "Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    // ── Applications ───────────────────────────────────────────────

    @GetMapping("/applications")
    @Operation(summary = "List all applications")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(
                applicationRepository.findAll().stream()
                        .map(this::toAppResponse)
                        .collect(Collectors.toList())
        );
    }

    private ApplicationResponse toAppResponse(Application app) {
        ApplicationResponse r = new ApplicationResponse();
        r.setId(app.getId());
        r.setStudentId(app.getStudent().getId());
        r.setJobId(app.getJob().getId());
        r.setJobTitle(app.getJob().getTitle());
        r.setCompanyName(app.getJob().getCompany().getCompanyName());
        r.setStatus(app.getStatus());
        r.setCoverLetter(app.getCoverLetter());
        r.setMatchScore(app.getMatchScore());
        r.setAppliedAt(app.getAppliedAt());
        return r;
    }

    // ── Skills ─────────────────────────────────────────────────────

    @PostMapping("/skills")
    @Operation(summary = "Create a skill")
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(skillService.createSkill(skill.getName(), skill.getCategory(), skill.getDescription()));
    }

    @PutMapping("/skills/{id}")
    @Operation(summary = "Update a skill")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.updateSkill(id, skill.getName(), skill.getCategory(), skill.getDescription()));
    }

    @DeleteMapping("/skills/{id}")
    @Operation(summary = "Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
