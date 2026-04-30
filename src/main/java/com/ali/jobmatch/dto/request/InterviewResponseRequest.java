package com.ali.jobmatch.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponseRequest {
    /** ACCEPT or RESCHEDULE */
    private String action;
    private String rescheduleNote;
}
