package com.ali.jobmatch.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {
    @Size(max = 255, message = "University name must not exceed 255 characters")
    private String university;

    @Size(max = 255, message = "Major must not exceed 255 characters")
    private String major;

    private LocalDate graduationDate;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    @URL(message = "Portfolio URL must be a valid URL")
    private String portfolioUrl;

    // skill ID -> level (1–5)
    private Map<Long, Integer> skills;

    // Job preferences
    private String preferredJobType;

    private String preferredSeniorityLevel;

    private Double expectedSalary;
}
