package com.ali.jobmatch.controller;

import com.ali.jobmatch.dto.request.ApplicationRequest;
import com.ali.jobmatch.dto.request.InterviewRequest;
import com.ali.jobmatch.dto.request.InterviewResponseRequest;
import com.ali.jobmatch.dto.request.ReviewNotesRequest;
import com.ali.jobmatch.dto.response.ApplicationResponse;
import com.ali.jobmatch.entity.Application;
import com.ali.jobmatch.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "Job application management endpoints")
@SecurityRequirement(name = "Bearer Token")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Apply for a job")
    public ResponseEntity<ApplicationResponse> applyForJob(@Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.applyForJob(request));
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<ApplicationResponse> getApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.getApplication(applicationId));
    }

    @GetMapping("/my-applications")
    @Operation(summary = "Get current user's applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getMyApplications());
    }

    @GetMapping("/check/{jobId}")
    @Operation(summary = "Check if the current student has already applied to a job")
    public ResponseEntity<Boolean> checkApplied(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.hasApplied(jobId));
    }

    @GetMapping("/company")
    @Operation(summary = "Get all applications for the current company's jobs")
    public ResponseEntity<List<ApplicationResponse>> getCompanyApplications() {
        return ResponseEntity.ok(applicationService.getCompanyApplications());
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Get all applications for a job")
    public ResponseEntity<List<ApplicationResponse>> getJobApplications(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getJobApplications(jobId));
    }

    @PatchMapping("/{applicationId}/status")
    @Operation(summary = "Update application status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam Application.ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(applicationId, status));
    }

    @PatchMapping("/{applicationId}/notes")
    @Operation(summary = "Update review notes for an application")
    public ResponseEntity<ApplicationResponse> updateReviewNotes(
            @PathVariable Long applicationId,
            @RequestBody ReviewNotesRequest request) {
        return ResponseEntity.ok(applicationService.updateReviewNotes(applicationId, request));
    }

    @PatchMapping("/{applicationId}/interview")
    @Operation(summary = "Schedule an interview for an application")
    public ResponseEntity<ApplicationResponse> scheduleInterview(
            @PathVariable Long applicationId,
            @RequestBody InterviewRequest request) {
        return ResponseEntity.ok(applicationService.scheduleInterview(applicationId, request));
    }

    @PatchMapping("/{applicationId}/interview-response")
    @Operation(summary = "Student accepts or requests reschedule of interview")
    public ResponseEntity<ApplicationResponse> respondToInterview(
            @PathVariable Long applicationId,
            @RequestBody InterviewResponseRequest request) {
        return ResponseEntity.ok(applicationService.respondToInterview(applicationId, request));
    }
}
