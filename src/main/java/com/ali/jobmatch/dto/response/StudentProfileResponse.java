package com.ali.jobmatch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String university;
    private String major;
    private LocalDate graduationDate;
    private String bio;
    private String portfolioUrl;
    private String resumeUrl;
    private String resumeOriginalName;
    private Map<String, Integer> skills; // skill name -> level
}
