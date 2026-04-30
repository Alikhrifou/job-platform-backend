package com.ali.jobmatch.dto.response;

import com.ali.jobmatch.entity.Application;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String studentName;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Application.ApplicationStatus status;
    private String coverLetter;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appliedAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reviewedAt;
    private String reviewNotes;
    private Double matchScore;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime interviewDate;
    private String interviewLink;
    private Boolean interviewConfirmed;
    private Boolean rescheduleRequested;
    private String rescheduleNote;
    private String studentEmail;
    private String studentUniversity;
    private String studentMajor;
    private String studentBio;
    private String studentPortfolioUrl;
    private String studentResumeUrl;
    private String studentResumeOriginalName;
}
