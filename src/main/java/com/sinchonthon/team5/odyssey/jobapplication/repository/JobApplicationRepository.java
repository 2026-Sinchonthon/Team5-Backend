package com.sinchonthon.team5.odyssey.jobapplication.repository;

import com.sinchonthon.team5.odyssey.jobapplication.domain.JobApplication;
import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    boolean existsByJobPostIdAndStudentId(
            Long jobPostId,
            Long studentId
    );

    List<JobApplication> findAllByStudentIdOrderByAppliedAtDesc(
            Long studentId
    );

    List<JobApplication> findAllByJobPostIdOrderByAppliedAtDesc(
            Long jobPostId
    );

    List<JobApplication> findAllByJobPostIdAndStatusOrderByAppliedAtDesc(
            Long jobPostId,
            JobApplicationStatus status
    );
}
