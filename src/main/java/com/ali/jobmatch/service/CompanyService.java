package com.ali.jobmatch.service;

import com.ali.jobmatch.dto.request.CompanyProfileRequest;
import com.ali.jobmatch.dto.response.CompanyProfileResponse;
import com.ali.jobmatch.entity.CompanyProfile;
import com.ali.jobmatch.entity.User;
import com.ali.jobmatch.exception.ResourceNotFoundException;
import com.ali.jobmatch.repository.CompanyProfileRepository;
import com.ali.jobmatch.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public CompanyProfileResponse createOrUpdateProfile(CompanyProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CompanyProfile companyProfile = companyProfileRepository.findByUserId(user.getId())
                .orElse(new CompanyProfile());

        modelMapper.map(request, companyProfile);
        companyProfile.setUser(user);

        companyProfileRepository.save(companyProfile);

        CompanyProfileResponse response = modelMapper.map(companyProfile, CompanyProfileResponse.class);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        return response;
    }

    public CompanyProfileResponse getProfile(Long companyId) {
        CompanyProfile companyProfile = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));

        CompanyProfileResponse response = modelMapper.map(companyProfile, CompanyProfileResponse.class);
        response.setUserId(companyProfile.getUser().getId());
        response.setEmail(companyProfile.getUser().getEmail());
        return response;
    }

    public CompanyProfileResponse getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CompanyProfile companyProfile = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));

        CompanyProfileResponse response = modelMapper.map(companyProfile, CompanyProfileResponse.class);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        return response;
    }
}
