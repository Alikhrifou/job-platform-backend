package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.request.ApplicationRequest;
import com.ali.jobmatch.dto.request.InterviewRequest;
import com.ali.jobmatch.dto.request.InterviewResponseRequest;
import com.ali.jobmatch.dto.request.ReviewNotesRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private NotificationService notificationService;

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

        // compute and store match score immediately on apply
        double score = matchingService.calculateMatchScore(studentProfile, application);
        application.setMatchScore(score);

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

    public boolean hasApplied(Long jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        StudentProfile studentProfile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return applicationRepository.findByStudentIdAndJobId(studentProfile.getId(), jobId).isPresent();
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
        application.setReviewedAt(java.time.LocalDateTime.now());
        applicationRepository.save(application);

        // Notify the student about the status change
        User student = application.getStudent().getUser();
        String jobTitle = application.getJob().getTitle();
        String title = "Application Update";
        String message = "Your application for \"" + jobTitle + "\" has been updated to: " + status.name().replace("_", " ");
        notificationService.createNotification(student, title, message, "/student/applications");

        return toResponse(application);
    }

    public ApplicationResponse updateReviewNotes(Long applicationId, ReviewNotesRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        application.setReviewNotes(request.getNotes());
        application.setReviewedAt(java.time.LocalDateTime.now());
        applicationRepository.save(application);
        return toResponse(application);
    }

    public ApplicationResponse scheduleInterview(Long applicationId, InterviewRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        application.setInterviewDate(request.getInterviewDate());
        application.setInterviewLink(request.getInterviewLink());
        application.setStatus(Application.ApplicationStatus.INTERVIEW_SCHEDULED);
        application.setInterviewConfirmed(false);
        application.setRescheduleRequested(false);
        application.setRescheduleNote(null);
        application.setReviewedAt(java.time.LocalDateTime.now());
        applicationRepository.save(application);

        // Notify the student
        User student = application.getStudent().getUser();
        String jobTitle = application.getJob().getTitle();
        notificationService.createNotification(
                student,
                "Interview Scheduled",
                "An interview has been scheduled for your application to \"" + jobTitle + "\". Please check the details.",
                "/student/applications"
        );

        return toResponse(application);
    }

    public ApplicationResponse respondToInterview(Long applicationId, InterviewResponseRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Ensure the application belongs to this student
        if (!application.getStudent().getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not authorized to respond to this interview");
        }

        if (!"ACCEPT".equalsIgnoreCase(request.getAction()) && !"RESCHEDULE".equalsIgnoreCase(request.getAction())) {
            throw new BadRequestException("Action must be ACCEPT or RESCHEDULE");
        }

        if ("ACCEPT".equalsIgnoreCase(request.getAction())) {
            application.setInterviewConfirmed(true);
            application.setRescheduleRequested(false);
            application.setRescheduleNote(null);
        } else {
            application.setRescheduleRequested(true);
            application.setInterviewConfirmed(false);
            application.setRescheduleNote(request.getRescheduleNote());

            // Notify the company that the student requested a reschedule
            User companyUser = application.getJob().getCompany().getUser();
            String studentName = application.getStudent().getUser().getFirstName()
                    + " " + application.getStudent().getUser().getLastName();
            notificationService.createNotification(
                    companyUser,
                    "Reschedule Requested",
                    studentName + " has requested to reschedule their interview for \"" + application.getJob().getTitle() + "\".",
                    "/company/applications/" + application.getJob().getId()
            );
        }
        applicationRepository.save(application);
        return toResponse(application);
    }

    public List<ApplicationResponse> getCompanyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return applicationRepository.findByJobCompanyUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getCompanyApplicationStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Application> applications = applicationRepository.findByJobCompanyUserId(user.getId());
        return applications.stream().collect(
                Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting())
        );
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
        response.setStudentBio(application.getStudent().getBio());
        response.setStudentPortfolioUrl(application.getStudent().getPortfolioUrl());
        response.setStudentResumeUrl(application.getStudent().getResumeUrl());
        response.setStudentResumeOriginalName(application.getStudent().getResumeOriginalName());
        return response;
    }
}
