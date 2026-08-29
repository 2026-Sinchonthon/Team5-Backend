package com.sinchonthon.team5.odyssey.jobapplication.service;

import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.jobapplication.JobApplicationErrorCode;
import com.sinchonthon.team5.odyssey.jobapplication.domain.JobApplication;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCancelResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCreateRequest;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCreateResponse;
import com.sinchonthon.team5.odyssey.jobapplication.repository.JobApplicationRepository;
import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.JobPostErrorCode;
import com.sinchonthon.team5.odyssey.jobpost.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;

    @Transactional
    public ApplicationCreateResponse apply(
            Long studentId,
            Long jobPostId,
            ApplicationCreateRequest request
    ) {
        JobPost jobPost = findJobPostOrThrow(jobPostId);

        validateJobPostOpen(jobPost);
        validateNotDuplicated(jobPostId, studentId);

        JobApplication application = JobApplication.create(
                jobPostId,
                studentId,
                request.message()
        );

        JobApplication savedApplication =
                jobApplicationRepository.save(application);

        return new ApplicationCreateResponse(
                savedApplication.getId(),
                savedApplication.getJobPostId(),
                savedApplication.getStatus(),
                savedApplication.getAppliedAt()
        );
    }

    @Transactional
    public ApplicationCancelResponse cancel(
            Long studentId,
            Long applicationId
    ) {
        JobApplication application =
                findApplicationOrThrow(applicationId);

        validateStudent(application, studentId);
        validateCancelable(application);

        application.cancel();

        return new ApplicationCancelResponse(
                application.getId(),
                application.getStatus()
        );
    }

    private JobPost findJobPostOrThrow(Long jobPostId) {
        return jobPostRepository.findById(jobPostId)
                .orElseThrow(() ->
                        new GeneralException(JobPostErrorCode.NOT_FOUND));
    }

    private JobApplication findApplicationOrThrow(Long applicationId) {
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new GeneralException(
                                JobApplicationErrorCode.NOT_FOUND
                        ));
    }

    private void validateJobPostOpen(JobPost jobPost) {
        if (!jobPost.isEditable()) {
            throw new GeneralException(
                    JobApplicationErrorCode.JOB_POST_NOT_OPEN
            );
        }
    }

    private void validateNotDuplicated(
            Long jobPostId,
            Long studentId
    ) {
        if (jobApplicationRepository
                .existsByJobPostIdAndStudentId(
                        jobPostId,
                        studentId
                )) {
            throw new GeneralException(
                    JobApplicationErrorCode.DUPLICATE_APPLICATION
            );
        }
    }

    private void validateStudent(
            JobApplication application,
            Long studentId
    ) {
        if (!application.isAppliedBy(studentId)) {
            throw new GeneralException(
                    JobApplicationErrorCode.FORBIDDEN
            );
        }
    }

    private void validateCancelable(JobApplication application) {
        if (!application.isPending()) {
            throw new GeneralException(
                    JobApplicationErrorCode.NOT_CANCELABLE
            );
        }
    }
}
