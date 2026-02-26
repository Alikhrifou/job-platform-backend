package com.ali.jobmatch.repository;

import com.ali.jobmatch.entity.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> findByCompanyId(Long companyId);
    List<JobOffer> findByIsActiveTrue();
    List<JobOffer> findByTitleContainingIgnoreCase(String title);
}
