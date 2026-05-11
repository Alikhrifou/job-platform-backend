package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.request.StudentProfileRequest;
import com.ali.jobmatch.dto.response.StudentProfileResponse;
import com.ali.jobmatch.entity.Skill;
import com.ali.jobmatch.entity.StudentProfile;
import com.ali.jobmatch.entity.StudentSkill;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.ResourceNotFoundException;
import com.ali.jobmatch.mapper.StudentMapper;
import com.ali.jobmatch.repository.SkillRepository;
import com.ali.jobmatch.repository.StudentProfileRepository;
import com.ali.jobmatch.repository.StudentSkillRepository;
import com.ali.jobmatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class StudentService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MatchingService matchingService;

    @Transactional
    public StudentProfileResponse createOrUpdateProfile(StudentProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile studentProfile = studentProfileRepository.findByUserId(user.getId())
                .orElse(new StudentProfile());

        studentProfile.setUser(user);
        studentProfile.setUniversity(request.getUniversity());
        studentProfile.setMajor(request.getMajor());
        studentProfile.setGraduationDate(request.getGraduationDate());
        studentProfile.setBio(request.getBio());
        studentProfile.setPortfolioUrl(request.getPortfolioUrl());
        // resumeUrl is managed separately via /profile/resume endpoint — do not overwrite here

        studentProfile = studentProfileRepository.save(studentProfile);

        // Update the managed collection in place to avoid composite-key identity conflicts.
        Map<Long, Integer> requestedSkills = request.getSkills() != null ? request.getSkills() : Map.of();
        Map<Long, StudentSkill> existingSkillsById = new HashMap<>();
        for (StudentSkill studentSkill : studentProfile.getSkills()) {
            existingSkillsById.put(studentSkill.getSkill().getId(), studentSkill);
        }

        studentProfile.getSkills().removeIf(studentSkill ->
                !requestedSkills.containsKey(studentSkill.getSkill().getId()));

        for (Map.Entry<Long, Integer> entry : requestedSkills.entrySet()) {
            Long skillId = entry.getKey();
            Integer level = entry.getValue();

            StudentSkill existingSkill = existingSkillsById.get(skillId);
            if (existingSkill != null) {
                existingSkill.setLevel(level);
                continue;
            }

            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));

            StudentSkill studentSkill = new StudentSkill();
            studentSkill.setId(new StudentSkill.StudentSkillId(studentProfile.getId(), skillId));
            studentSkill.setStudent(studentProfile);
            studentSkill.setSkill(skill);
            studentSkill.setLevel(level);
            studentProfile.getSkills().add(studentSkill);
        }

        studentProfile = studentProfileRepository.save(studentProfile);

        // Recalculate match scores for all existing applications of this student
        // so that adding/updating skills retroactively updates the score.
        matchingService.recalculateScoresForStudent(studentProfile.getId());

        return studentMapper.toResponse(studentProfile);
    }

    public StudentProfileResponse getProfile(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return studentMapper.toResponse(studentProfile);
    }

    public StudentProfileResponse getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile studentProfile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return studentMapper.toResponse(studentProfile);
    }

    @Transactional
    public String uploadResume(MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    StudentProfile newProfile = new StudentProfile();
                    newProfile.setUser(user);
                    return studentProfileRepository.save(newProfile);
                });

        // Delete old file if exists
        if (profile.getResumeUrl() != null && !profile.getResumeUrl().isBlank()) {
            fileStorageService.deleteResume(profile.getResumeUrl());
        }

        String filename = fileStorageService.storeResume(file, user.getId());
        profile.setResumeUrl(filename);
        profile.setResumeOriginalName(file.getOriginalFilename());
        studentProfileRepository.save(profile);
        return filename;
    }

    @Transactional
    public void deleteResume() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        if (profile.getResumeUrl() != null && !profile.getResumeUrl().isBlank()) {
            fileStorageService.deleteResume(profile.getResumeUrl());
            profile.setResumeUrl(null);
            profile.setResumeOriginalName(null);
            studentProfileRepository.save(profile);
        }
    }
}
