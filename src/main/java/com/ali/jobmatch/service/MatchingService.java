package com.ali.jobmatch.service;

import com.ali.jobmatch.entity.Application;
import com.ali.jobmatch.entity.JobSkill;
import com.ali.jobmatch.entity.StudentProfile;
import com.ali.jobmatch.entity.StudentSkill;
import com.ali.jobmatch.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Score = weighted average of:
     * - Skill match   (80%): for each required skill, student gets points proportional
     *   to their level vs required level (capped at 100% per skill)
     * - CV keywords   (20%): % of required skill names found in the resume text.
     *   If no resume uploaded the weight is fully attributed to skills (normalized to 100%).
     */
    public Double calculateMatchScore(StudentProfile studentProfile, Application application) {
        Map<Long, Integer> studentSkillMap = studentProfile.getSkills().stream()
                .collect(Collectors.toMap(
                        ss -> ss.getSkill().getId(),
                        StudentSkill::getLevel
                ));

        List<JobSkill> requiredSkills = application.getJob().getRequiredSkills();

        if (requiredSkills.isEmpty()) {
            return 0.0;
        }

        double skillScore = requiredSkills.stream().mapToDouble(jobSkill -> {
            Integer studentLevel = studentSkillMap.get(jobSkill.getSkill().getId());
            if (studentLevel == null) return 0.0;
            return Math.min((double) studentLevel / jobSkill.getRequiredLevel(), 1.0) * 100;
        }).average().orElse(0.0);

        String resumeText = fileStorageService.readResumeText(studentProfile.getResumeUrl());

        if (resumeText.isBlank()) {
            // No CV uploaded — skill score is the full score
            return skillScore;
        }

        double cvKeywordScore = calculateCvKeywordScore(resumeText, requiredSkills);

        return (skillScore * 0.80) + (cvKeywordScore * 0.20);
    }

    private double calculateCvKeywordScore(String resumeText, List<JobSkill> requiredSkills) {
        if (resumeText.isBlank() || requiredSkills.isEmpty()) return 0.0;
        String lower = resumeText.toLowerCase();
        long matches = requiredSkills.stream()
                .filter(js -> lower.contains(js.getSkill().getName().toLowerCase()))
                .count();
        return ((double) matches / requiredSkills.size()) * 100;
    }

    public void updateAllMatchScores() {
        applicationRepository.findAll().forEach(application -> {
            double score = calculateMatchScore(application.getStudent(), application);
            application.setMatchScore(score);
            applicationRepository.save(application);
        });
    }
}

