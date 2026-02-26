package com.ali.jobmatch.controller;

import com.ali.jobmatch.dto.request.StudentProfileRequest;
import com.ali.jobmatch.dto.response.StudentProfileResponse;
import com.ali.jobmatch.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Student profile management endpoints")
@SecurityRequirement(name = "Bearer Token")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/profile")
    @Operation(summary = "Create or update student profile")
    public ResponseEntity<StudentProfileResponse> createOrUpdateProfile(
            @Valid @RequestBody StudentProfileRequest request) {
        return ResponseEntity.ok(studentService.createOrUpdateProfile(request));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current student profile")
    public ResponseEntity<StudentProfileResponse> getMyProfile() {
        return ResponseEntity.ok(studentService.getMyProfile());
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get student profile by ID")
    public ResponseEntity<StudentProfileResponse> getProfile(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getProfile(studentId));
    }
}
