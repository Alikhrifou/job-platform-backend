package com.ali.jobmatch.controller;

import com.ali.jobmatch.dto.request.JobOfferRequest;
import com.ali.jobmatch.dto.response.JobOfferResponse;
import com.ali.jobmatch.service.JobService;
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
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Job offer management endpoints")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping
    @Operation(summary = "Create a new job offer")
    @SecurityRequirement(name = "Bearer Token")
    public ResponseEntity<JobOfferResponse> createJob(@Valid @RequestBody JobOfferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(request));
    }

    @PutMapping("/{jobId}")
    @Operation(summary = "Update a job offer")
    @SecurityRequirement(name = "Bearer Token")
    public ResponseEntity<JobOfferResponse> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody JobOfferRequest request) {
        return ResponseEntity.ok(jobService.updateJob(jobId, request));
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get a job offer by ID")
    public ResponseEntity<JobOfferResponse> getJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }

    @GetMapping
    @Operation(summary = "Get all active jobs")
    public ResponseEntity<List<JobOfferResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    @GetMapping("/search")
    @Operation(summary = "Search jobs with filters")
    public ResponseEntity<List<JobOfferResponse>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String seniorityLevel,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary) {
        return ResponseEntity.ok(jobService.searchJobs(title, city, jobType, seniorityLevel, minSalary, maxSalary));
    }

    @GetMapping("/my-jobs")
    @Operation(summary = "Get all jobs for the authenticated company")
    @SecurityRequirement(name = "Bearer Token")
    public ResponseEntity<List<JobOfferResponse>> getMyJobs() {
        return ResponseEntity.ok(jobService.getMyCompanyJobs());
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get all jobs by company")
    public ResponseEntity<List<JobOfferResponse>> getCompanyJobs(@PathVariable Long companyId) {
        return ResponseEntity.ok(jobService.getCompanyJobs(companyId));
    }

    @DeleteMapping("/{jobId}")
    @Operation(summary = "Delete a job offer")
    @SecurityRequirement(name = "Bearer Token")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }
}
