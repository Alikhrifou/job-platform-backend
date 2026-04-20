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
        studentProfile.setResumeUrl(request.getResumeUrl());
        studentProfile.setGpa(request.getGpa());

        studentProfile = studentProfileRepository.save(studentProfile);

        // Manage skills through the entity collection so it stays in sync
        studentProfile.getSkills().clear();
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            for (Map.Entry<Long, Integer> entry : request.getSkills().entrySet()) {
                Skill skill = skillRepository.findById(entry.getKey())
                        .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + entry.getKey()));
                StudentSkill studentSkill = new StudentSkill();
                studentSkill.setStudent(studentProfile);
                studentSkill.setSkill(skill);
                studentSkill.setLevel(entry.getValue());
                studentProfile.getSkills().add(studentSkill);
            }
        }
        studentProfile = studentProfileRepository.save(studentProfile);

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
