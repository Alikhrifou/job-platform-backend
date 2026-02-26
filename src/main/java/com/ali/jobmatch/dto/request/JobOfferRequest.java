package com.ali.jobmatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String title;

    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    // INTERNSHIP, JOB, PART_TIME, CONTRACT
    private String jobType;

    private Double salary;

    private String salaryRange;

    private LocalDateTime closingDate;

    // skill ID -> required level (1–5)
    private Map<Long, Integer> requiredSkills;

    @NotNull(message = "Active status is required")
    private Boolean isActive = true;
}
