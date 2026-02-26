package com.ali.jobmatch.controller;

import com.ali.jobmatch.dto.request.CompanyProfileRequest;
import com.ali.jobmatch.dto.response.CompanyProfileResponse;
import com.ali.jobmatch.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@Tag(name = "Companies", description = "Company profile management endpoints")
@SecurityRequirement(name = "Bearer Token")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/profile")
    @Operation(summary = "Create or update company profile")
    public ResponseEntity<CompanyProfileResponse> createOrUpdateProfile(
            @Valid @RequestBody CompanyProfileRequest request) {
        return ResponseEntity.ok(companyService.createOrUpdateProfile(request));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current company profile")
    public ResponseEntity<CompanyProfileResponse> getMyProfile() {
        return ResponseEntity.ok(companyService.getMyProfile());
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "Get company profile by ID")
    public ResponseEntity<CompanyProfileResponse> getProfile(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyService.getProfile(companyId));
    }
}
