package com.ali.jobmatch.repository;

import com.ali.jobmatch.entity.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkill.JobSkillId> {
    List<JobSkill> findByJobId(Long jobId);
    void deleteByJobId(Long jobId);
}
