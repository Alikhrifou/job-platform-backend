package com.ali.jobmatch.mapper;

import com.ali.jobmatch.dto.response.JobOfferResponse;
import com.ali.jobmatch.entity.JobOffer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JobMapper {

    @Autowired
    private ModelMapper modelMapper;

    public JobOfferResponse toResponse(JobOffer jobOffer, Integer applicationsCount) {
        JobOfferResponse response = modelMapper.map(jobOffer, JobOfferResponse.class);
        response.setCompanyId(jobOffer.getCompany().getId());
        response.setCompanyName(jobOffer.getCompany().getCompanyName());
        response.setRequiredSkills(jobOffer.getRequiredSkills().stream()
                .collect(Collectors.toMap(
                        js -> js.getSkill().getName(),
                        js -> js.getRequiredLevel()
                )));
        response.setApplicationsCount(applicationsCount);
        return response;
    }

    public JobOfferResponse toResponse(JobOffer jobOffer) {
        return toResponse(jobOffer, 0);
    }
}
