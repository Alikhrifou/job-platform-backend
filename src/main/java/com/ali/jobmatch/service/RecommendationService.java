package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.response.RecommendedJobResponse;
import com.ali.jobmatch.entity.JobOffer;
import com.ali.jobmatch.entity.StudentProfile;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.ResourceNotFoundException;
import com.ali.jobmatch.mapper.JobMapper;
import com.ali.jobmatch.repository.ApplicationRepository;
import com.ali.jobmatch.repository.JobRepository;
import com.ali.jobmatch.repository.StudentProfileRepository;
import com.ali.jobmatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentProfileRepository studentProfileRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private JobMapper jobMapper;

    /**
     * Returns up to 10 recommended active jobs for the current student,
     * ranked by a score (0–100) based on:
     *   - Skill overlap      : 50 pts max
     *   - Job type match     : 20 pts
     *   - Seniority match    : 20 pts
     *   - Salary fit         : 10 pts
     *
     * Already-applied jobs are excluded.
     */
    public List<RecommendedJobResponse> getRecommendations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile student = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        // IDs of jobs the student already applied to
        Set<Long> appliedJobIds = applicationRepository.findByStudentId(student.getId())
                .stream()
                .map(a -> a.getJob().getId())
                .collect(Collectors.toSet());

        // Student's skill IDs for fast lookup
        Set<Long> studentSkillIds = student.getSkills().stream()
                .map(ss -> ss.getSkill().getId())
                .collect(Collectors.toSet());

        return jobRepository.findByIsActiveTrue().stream()
                .filter(job -> !appliedJobIds.contains(job.getId()))
                .map(job -> score(job, student, studentSkillIds))
                .filter(r -> r.getMatchScore() > 0)
                .sorted(Comparator.comparingInt(RecommendedJobResponse::getMatchScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private RecommendedJobResponse score(JobOffer job, StudentProfile student, Set<Long> studentSkillIds) {
        int score = 0;

        // 1. Skill overlap (50 pts max)
        int totalSkills = job.getRequiredSkills().size();
        int matched = (int) job.getRequiredSkills().stream()
                .filter(js -> studentSkillIds.contains(js.getSkill().getId()))
                .count();
        if (totalSkills > 0) {
            score += (int) Math.round((double) matched / totalSkills * 50);
        }

        // 2. Job type match (20 pts)
        if (student.getPreferredJobType() != null
                && student.getPreferredJobType().equalsIgnoreCase(job.getJobType().name())) {
            score += 20;
        }

        // 3. Seniority match (20 pts)
        if (student.getPreferredSeniorityLevel() != null
                && job.getSeniorityLevel() != null
                && student.getPreferredSeniorityLevel().equalsIgnoreCase(job.getSeniorityLevel().name())) {
            score += 20;
        }

        // 4. Salary fit (10 pts) — job pays at least what the student expects
        if (student.getExpectedSalary() != null
                && job.getSalary() != null
                && job.getSalary() >= student.getExpectedSalary()) {
            score += 10;
        }

        return new RecommendedJobResponse(jobMapper.toResponse(job), score, matched, totalSkills);
    }
}
