package com.ali.jobmatch.repository;

import com.ali.jobmatch.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    Optional<CompanyProfile> findByUserId(Long userId);
    Optional<CompanyProfile> findByCompanyName(String companyName);
}
