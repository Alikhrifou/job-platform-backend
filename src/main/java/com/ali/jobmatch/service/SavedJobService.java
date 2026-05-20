package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.response.JobOfferResponse;
import com.ali.jobmatch.entity.JobOffer;
import com.ali.jobmatch.entity.SavedJob;
import com.ali.jobmatch.entity.StudentProfile;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.BadRequestException;
import com.ali.jobmatch.exception.ResourceNotFoundException;
import com.ali.jobmatch.mapper.JobMapper;
import com.ali.jobmatch.repository.JobRepository;
import com.ali.jobmatch.repository.SavedJobRepository;
import com.ali.jobmatch.repository.StudentProfileRepository;
import com.ali.jobmatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedJobService {

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobMapper jobMapper;

    private StudentProfile getCurrentStudentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
    }

    public List<JobOfferResponse> getSavedJobs() {
        StudentProfile student = getCurrentStudentProfile();
        return savedJobRepository.findByStudentOrderBySavedAtDesc(student)
                .stream()
                .map(saved -> jobMapper.toResponse(saved.getJob()))
                .collect(Collectors.toList());
    }

    public void saveJob(Long jobId) {
        StudentProfile student = getCurrentStudentProfile();
        if (savedJobRepository.existsByStudentAndJobId(student, jobId)) {
            throw new BadRequestException("Job already saved");
        }
        JobOffer job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        SavedJob savedJob = SavedJob.builder()
                .student(student)
                .job(job)
                .build();
        savedJobRepository.save(savedJob);
    }

    @Transactional
    public void unsaveJob(Long jobId) {
        StudentProfile student = getCurrentStudentProfile();
        savedJobRepository.deleteByStudentAndJobId(student, jobId);
    }

    public boolean isJobSaved(Long jobId) {
        StudentProfile student = getCurrentStudentProfile();
        return savedJobRepository.existsByStudentAndJobId(student, jobId);
    }
}
