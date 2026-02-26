package com.ali.jobmatch.dto.response;

import com.ali.jobmatch.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long studentId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Application.ApplicationStatus status;
    private String coverLetter;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private String reviewNotes;
    private Double matchScore;
}
