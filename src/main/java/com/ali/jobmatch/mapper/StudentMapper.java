package com.ali.jobmatch.mapper;

import com.ali.jobmatch.dto.response.StudentProfileResponse;
import com.ali.jobmatch.entity.StudentProfile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public StudentProfileResponse toResponse(StudentProfile studentProfile) {
        StudentProfileResponse response = new StudentProfileResponse();
        response.setId(studentProfile.getId());
        response.setUserId(studentProfile.getUser().getId());
        response.setEmail(studentProfile.getUser().getEmail());
        response.setFullName(studentProfile.getUser().getFirstName() + " " + studentProfile.getUser().getLastName());
        response.setUniversity(studentProfile.getUniversity());
        response.setMajor(studentProfile.getMajor());
        response.setGraduationDate(studentProfile.getGraduationDate());
        response.setBio(studentProfile.getBio());
        response.setPortfolioUrl(studentProfile.getPortfolioUrl());
        response.setResumeUrl(studentProfile.getResumeUrl());
        response.setResumeOriginalName(studentProfile.getResumeOriginalName());
        response.setSkills(studentProfile.getSkills().stream()
                .collect(Collectors.toMap(
                        ss -> ss.getSkill().getName(),
                        ss -> ss.getLevel()
                )));
        return response;
    }
}
