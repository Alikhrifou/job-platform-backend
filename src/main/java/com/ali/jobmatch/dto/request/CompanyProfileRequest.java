package com.ali.jobmatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileRequest {
    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @NotBlank(message = "Industry is required")
    @Size(max = 255, message = "Industry must not exceed 255 characters")
    private String industry;

    @URL(message = "Website must be a valid URL")
    private String website;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @URL(message = "Logo URL must be a valid URL")
    private String logoUrl;

    private Integer employeeCount;
}
