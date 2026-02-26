package com.ali.jobmatch.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {
    private String university;
    private String major;
    private LocalDate graduationDate;
    private String bio;
    private String portfolioUrl;
    private String resumeUrl;

    @DecimalMin(value = "0.0", message = "GPA must be positive")
    private Double gpa;

    // skill ID -> level (1–5)
    private Map<Long, Integer> skills;
}
