package com.ali.jobmatch.repository;

import com.ali.jobmatch.entity.SavedJob;
import com.ali.jobmatch.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    List<SavedJob> findByStudentOrderBySavedAtDesc(StudentProfile student);

    Optional<SavedJob> findByStudentAndJobId(StudentProfile student, Long jobId);

    boolean existsByStudentAndJobId(StudentProfile student, Long jobId);

    void deleteByStudentAndJobId(StudentProfile student, Long jobId);
}
