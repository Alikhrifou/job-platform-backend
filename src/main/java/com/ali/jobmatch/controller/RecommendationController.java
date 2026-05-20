package com.ali.jobmatch.controller;

import com.ali.jobmatch.dto.response.RecommendedJobResponse;
import com.ali.jobmatch.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<RecommendedJobResponse>> getRecommendations() {
        return ResponseEntity.ok(recommendationService.getRecommendations());
    }
}
