package com.sinchonthon.team5.odyssey.jobapplication.repository;

import com.sinchonthon.team5.odyssey.jobapplication.domain.JobApplication;
import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

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

    @Query("""
            select
                a.id as applicationId,
                j.id as jobPostId,
                j.title as title,
                o.businessName as businessName,
                j.budget as budget,
                j.deadline as deadline,
                a.status as status,
                a.appliedAt as appliedAt
            from JobApplication a
            join JobPost j on j.id = a.jobPostId
            join OwnerProfile o on o.memberId = j.ownerId
            where a.studentId = :studentId
            order by a.appliedAt desc
            """)
    List<ApplicationSummaryProjection> findApplicationSummariesByStudentId(
            @Param("studentId") Long studentId
    );

    @Query("""
            select
                a.id as applicationId,
                m.id as memberId,
                m.name as name,
                s.universityId as universityId,
                s.major as major,
                s.introduction as introduction,
                a.message as message,
                a.status as status,
                a.appliedAt as appliedAt
            from JobApplication a
            join Member m on m.id = a.studentId
            join StudentProfile s on s.memberId = m.id
            where a.jobPostId = :jobPostId
            order by a.appliedAt desc
            """)
    List<ApplicantProjection> findApplicantProjectionsByJobPostId(
            @Param("jobPostId") Long jobPostId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a
            from JobApplication a
            where a.jobPostId = :jobPostId
            order by a.id
            """)
    List<JobApplication> findAllByJobPostIdForUpdate(
            @Param("jobPostId") Long jobPostId
    );
}
