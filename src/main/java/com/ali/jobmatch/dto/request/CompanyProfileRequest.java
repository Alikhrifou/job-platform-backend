package com.ali.jobmatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Industry is required")
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
