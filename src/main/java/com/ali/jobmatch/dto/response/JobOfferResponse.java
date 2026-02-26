package com.ali.jobmatch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String description;
    private String location;
    private String jobType;
    private Double salary;
    private String salaryRange;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime closingDate;
    private Map<String, Integer> requiredSkills; // skill name -> required level
    private Integer applicationsCount;
}
