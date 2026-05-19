package com.ali.jobmatch.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    // INTERNSHIP, JOB, PART_TIME, CONTRACT
    private String jobType;

    // JUNIOR, MID, SENIOR
    private String seniorityLevel;

    @Min(value = 0, message = "Salary must be a positive value")
    private Double salary;

    @Size(max = 100, message = "Salary range must not exceed 100 characters")
    private String salaryRange;

    private LocalDateTime closingDate;

    // skill ID -> required level (1–5)
    private Map<Long, Integer> requiredSkills;

    @NotNull(message = "Active status is required")
    private Boolean isActive = true;
}
