package com.ali.jobmatch.dto.request;

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

    // skill ID -> level (1–5)
    private Map<Long, Integer> skills;
}
