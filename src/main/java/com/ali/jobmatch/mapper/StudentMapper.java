package com.ali.jobmatch.mapper;

import com.ali.jobmatch.dto.response.StudentProfileResponse;
import com.ali.jobmatch.entity.StudentProfile;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    @Autowired
    private ModelMapper modelMapper;

    public StudentProfileResponse toResponse(StudentProfile studentProfile) {
        StudentProfileResponse response = modelMapper.map(studentProfile, StudentProfileResponse.class);
        response.setUserId(studentProfile.getUser().getId());
        response.setEmail(studentProfile.getUser().getEmail());
        response.setFullName(studentProfile.getUser().getFirstName() + " " + studentProfile.getUser().getLastName());
        response.setSkills(studentProfile.getSkills().stream()
                .collect(Collectors.toMap(
                        ss -> ss.getSkill().getName(),
                        ss -> ss.getLevel()
                )));
        return response;
    }
}
