package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.request.ApplicationRequest;
import com.ali.jobmatch.dto.response.ApplicationResponse;
import com.ali.jobmatch.entity.Application;
import com.ali.jobmatch.entity.JobOffer;
import com.ali.jobmatch.entity.StudentProfile;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.BadRequestException;
import com.ali.jobmatch.exception.ResourceNotFoundException;
import com.ali.jobmatch.repository.ApplicationRepository;
import com.ali.jobmatch.repository.JobRepository;
import com.ali.jobmatch.repository.StudentProfileRepository;
import com.ali.jobmatch.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ApplicationResponse applyForJob(ApplicationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile studentProfile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        JobOffer jobOffer = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (applicationRepository.findByStudentIdAndJobId(studentProfile.getId(), jobOffer.getId()).isPresent()) {
            throw new BadRequestException("Already applied for this job");
        }

        Application application = new Application();
        application.setStudent(studentProfile);
        application.setJob(jobOffer);
        application.setCoverLetter(request.getCoverLetter());
        application.setStatus(Application.ApplicationStatus.PENDING);

        applicationRepository.save(application);
        return toResponse(application);
    }

    public ApplicationResponse getApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        return toResponse(application);
    }

    public List<ApplicationResponse> getMyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile studentProfile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return applicationRepository.findByStudentId(studentProfile.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getJobApplications(Long jobId) {
        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ApplicationResponse updateApplicationStatus(Long applicationId, Application.ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        application.setStatus(status);
        applicationRepository.save(application);
        return toResponse(application);
    }

    public List<ApplicationResponse> getCompanyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return applicationRepository.findAll().stream()
                .filter(a -> a.getJob().getCompany().getUser().getId().equals(user.getId()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ApplicationResponse toResponse(Application application) {
        ApplicationResponse response = modelMapper.map(application, ApplicationResponse.class);
        response.setStudentId(application.getStudent().getId());
        response.setJobId(application.getJob().getId());
        response.setJobTitle(application.getJob().getTitle());
        response.setCompanyName(application.getJob().getCompany().getCompanyName());
        response.setStudentName(application.getStudent().getUser().getFirstName() + " " + application.getStudent().getUser().getLastName());
        response.setStudentEmail(application.getStudent().getUser().getEmail());
        response.setStudentUniversity(application.getStudent().getUniversity());
        response.setStudentMajor(application.getStudent().getMajor());
        response.setStudentGpa(application.getStudent().getGpa());
        response.setStudentBio(application.getStudent().getBio());
        response.setStudentPortfolioUrl(application.getStudent().getPortfolioUrl());
        response.setStudentResumeUrl(application.getStudent().getResumeUrl());
        return response;
    }
}
