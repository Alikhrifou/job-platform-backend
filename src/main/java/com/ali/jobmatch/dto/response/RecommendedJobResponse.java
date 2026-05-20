package com.ali.jobmatch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedJobResponse {
    private JobOfferResponse job;
    private int matchScore;      // 0-100
    private int skillMatches;    // number of matching skills
    private int totalSkills;     // total required skills in this job
}
