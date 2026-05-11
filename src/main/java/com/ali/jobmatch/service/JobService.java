package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.request.JobOfferRequest;
import com.ali.jobmatch.dto.response.JobOfferResponse;
import com.ali.jobmatch.entity.CompanyProfile;
import com.ali.jobmatch.entity.JobOffer;
import com.ali.jobmatch.entity.JobSkill;
import com.ali.jobmatch.entity.Skill;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.ResourceNotFoundException;
import com.ali.jobmatch.mapper.JobMapper;
import com.ali.jobmatch.repository.CompanyProfileRepository;
import com.ali.jobmatch.repository.JobRepository;
import com.ali.jobmatch.repository.JobSkillRepository;
import com.ali.jobmatch.repository.SkillRepository;
import com.ali.jobmatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private JobSkillRepository jobSkillRepository;

    @Autowired
    private com.ali.jobmatch.repository.ApplicationRepository applicationRepository;

    @Autowired
    private JobMapper jobMapper;

    @Transactional
    public JobOfferResponse createJob(JobOfferRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CompanyProfile companyProfile = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));

        JobOffer jobOffer = new JobOffer();
        jobOffer.setCompany(companyProfile);
        jobOffer.setTitle(request.getTitle());
        jobOffer.setDescription(request.getDescription());
        jobOffer.setLocation(request.getLocation());
        if (request.getJobType() != null) {
            jobOffer.setJobType(JobOffer.JobType.valueOf(request.getJobType().toUpperCase()));
        }
        jobOffer.setSalary(request.getSalary());
        jobOffer.setSalaryRange(request.getSalaryRange());
        jobOffer.setClosingDate(request.getClosingDate());
        jobOffer.setIsActive(request.getIsActive());

        jobOffer = jobRepository.save(jobOffer);
        saveJobSkills(jobOffer, request.getRequiredSkills());
        return jobMapper.toResponse(jobOffer);
    }

    @Transactional
    public JobOfferResponse updateJob(Long jobId, JobOfferRequest request) {
        JobOffer jobOffer = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        jobOffer.setTitle(request.getTitle());
        jobOffer.setDescription(request.getDescription());
        jobOffer.setLocation(request.getLocation());
        if (request.getJobType() != null) {
            jobOffer.setJobType(JobOffer.JobType.valueOf(request.getJobType().toUpperCase()));
        }
        jobOffer.setSalary(request.getSalary());
        jobOffer.setSalaryRange(request.getSalaryRange());
        jobOffer.setClosingDate(request.getClosingDate());
        jobOffer.setIsActive(request.getIsActive());

        jobOffer = jobRepository.save(jobOffer);
        if (request.getRequiredSkills() != null) {
            jobSkillRepository.deleteByJobId(jobOffer.getId());
            saveJobSkills(jobOffer, request.getRequiredSkills());
        }
        return jobMapper.toResponse(jobOffer);
    }

    private void saveJobSkills(JobOffer jobOffer, Map<Long, Integer> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return;
        for (Map.Entry<Long, Integer> entry : requiredSkills.entrySet()) {
            Skill skill = skillRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + entry.getKey()));
            JobSkill jobSkill = new JobSkill();
            jobSkill.setJob(jobOffer);
            jobSkill.setSkill(skill);
            jobSkill.setRequiredLevel(entry.getValue());
            jobSkillRepository.save(jobSkill);
        }
    }

    public JobOfferResponse getJob(Long jobId) {
        JobOffer jobOffer = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return jobMapper.toResponse(jobOffer);
    }

    public List<JobOfferResponse> getAllActiveJobs() {
        return jobRepository.findByIsActiveTrue()
                .stream()
                .map(jobOffer -> jobMapper.toResponse(jobOffer))
                .collect(Collectors.toList());
    }

    public List<JobOfferResponse> searchJobs(String title, String city) {
        boolean hasTitle = title != null && !title.isBlank();
        boolean hasCity  = city  != null && !city.isBlank();
        List<JobOffer> results;
        if (hasTitle && hasCity) {
            results = jobRepository.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(title, city);
        } else if (hasTitle) {
            results = jobRepository.findByTitleContainingIgnoreCase(title);
        } else if (hasCity) {
            results = jobRepository.findByLocationContainingIgnoreCase(city);
        } else {
            results = jobRepository.findByIsActiveTrue();
        }
        return results.stream().map(jobMapper::toResponse).collect(Collectors.toList());
    }

    public List<JobOfferResponse> getCompanyJobs(Long companyId) {
        return jobRepository.findByCompanyId(companyId)
                .stream()
                .map(jobOffer -> jobMapper.toResponse(jobOffer))
                .collect(Collectors.toList());
    }

    public List<JobOfferResponse> getMyCompanyJobs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CompanyProfile companyProfile = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));
        return jobRepository.findByCompanyId(companyProfile.getId())
                .stream()
                .map(jobOffer -> jobMapper.toResponse(jobOffer))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteJob(Long jobId) {
        JobOffer jobOffer = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        applicationRepository.deleteByJobId(jobId);
        jobRepository.delete(jobOffer);
    }
}

