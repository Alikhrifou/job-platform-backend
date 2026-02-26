package com.ali.jobmatch.service;

import com.ali.jobmatch.entity.Application;
import com.ali.jobmatch.entity.JobSkill;
import com.ali.jobmatch.entity.StudentProfile;
import com.ali.jobmatch.entity.StudentSkill;
import com.ali.jobmatch.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * Score = weighted average of:
     * - Skill match (70%): for each required skill, student gets points proportional
     *   to their level vs required level (capped at 100% per skill)
     * - GPA score (30%): gpa / 4.0 * 100
     */
    public Double calculateMatchScore(StudentProfile studentProfile, Application application) {
        // Build map: skillId -> student level
        Map<Long, Integer> studentSkillMap = studentProfile.getSkills().stream()
                .collect(Collectors.toMap(
                        ss -> ss.getSkill().getId(),
                        ss -> ss.getLevel()
                ));

        java.util.List<JobSkill> requiredSkills = application.getJob().getRequiredSkills();

        if (requiredSkills.isEmpty()) {
            double gpaScore = (studentProfile.getGpa() / 4.0) * 100;
            return Math.min(gpaScore, 100.0);
        }

        double skillScore = requiredSkills.stream().mapToDouble(jobSkill -> {
            Integer studentLevel = studentSkillMap.get(jobSkill.getSkill().getId());
            if (studentLevel == null) return 0.0;
            // full points if student level >= required, partial otherwise
            return Math.min((double) studentLevel / jobSkill.getRequiredLevel(), 1.0) * 100;
        }).average().orElse(0.0);

        double gpaScore = (studentProfile.getGpa() / 4.0) * 100;

        return (skillScore * 0.7) + (gpaScore * 0.3);
    }

    public void updateAllMatchScores() {
        applicationRepository.findAll().forEach(application -> {
            double score = calculateMatchScore(application.getStudent(), application);
            application.setMatchScore(score);
            applicationRepository.save(application);
        });
    }
}
