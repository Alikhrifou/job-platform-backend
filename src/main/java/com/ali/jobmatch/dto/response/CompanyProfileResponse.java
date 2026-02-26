package com.ali.jobmatch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponse {
    private Long id;
    private Long userId;
    private String email;
    private String companyName;
    private String industry;
    private String website;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String description;
    private String logoUrl;
    private Integer employeeCount;
}
