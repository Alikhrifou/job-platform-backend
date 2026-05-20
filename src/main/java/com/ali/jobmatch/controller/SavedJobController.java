package com.ali.jobmatch.controller;

import com.ali.jobmatch.dto.response.JobOfferResponse;
import com.ali.jobmatch.service.SavedJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved-jobs")
@Tag(name = "Saved Jobs", description = "Student bookmarked jobs")
@SecurityRequirement(name = "Bearer Token")
public class SavedJobController {

    @Autowired
    private SavedJobService savedJobService;

    @GetMapping
    @Operation(summary = "Get all saved jobs for the current student")
    public ResponseEntity<List<JobOfferResponse>> getSavedJobs() {
        return ResponseEntity.ok(savedJobService.getSavedJobs());
    }

    @PostMapping("/{jobId}")
    @Operation(summary = "Save a job offer")
    public ResponseEntity<Void> saveJob(@PathVariable Long jobId) {
        savedJobService.saveJob(jobId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{jobId}")
    @Operation(summary = "Remove a saved job offer")
    public ResponseEntity<Void> unsaveJob(@PathVariable Long jobId) {
        savedJobService.unsaveJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{jobId}")
    @Operation(summary = "Check if a job is saved by the current student")
    public ResponseEntity<Map<String, Boolean>> isJobSaved(@PathVariable Long jobId) {
        return ResponseEntity.ok(Map.of("saved", savedJobService.isJobSaved(jobId)));
    }
}
