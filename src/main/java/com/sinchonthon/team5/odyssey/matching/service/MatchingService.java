package com.sinchonthon.team5.odyssey.matching.service;

import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.jobapplication.JobApplicationErrorCode;
import com.sinchonthon.team5.odyssey.jobapplication.domain.JobApplication;
import com.sinchonthon.team5.odyssey.jobapplication.repository.JobApplicationRepository;
import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.JobPostErrorCode;
import com.sinchonthon.team5.odyssey.jobpost.JobPostRepository;
import com.sinchonthon.team5.odyssey.matching.MatchingErrorCode;
import com.sinchonthon.team5.odyssey.matching.domain.Matching;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingAcceptRequest;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingCreateResponse;
import com.sinchonthon.team5.odyssey.matching.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchingRepository matchingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;

    @Transactional
    public MatchingCreateResponse accept(
            Long ownerId,
            Long applicationId,
            MatchingAcceptRequest request
    ) {
        JobApplication initialApplication = findApplicationOrThrow(applicationId);
        JobPost jobPost = findJobPostForUpdateOrThrow(initialApplication.getJobPostId());

        validateOwner(jobPost, ownerId);
        validateMatchable(jobPost);

        List<JobApplication> applications =
                jobApplicationRepository.findAllByJobPostIdForUpdate(jobPost.getId());

        JobApplication selectedApplication = applications.stream()
                .filter(application -> application.getId().equals(applicationId))
                .findFirst()
                .orElseThrow(() -> new GeneralException(JobApplicationErrorCode.NOT_FOUND));

        if (!selectedApplication.isPending()) {
            throw new GeneralException(MatchingErrorCode.APPLICATION_NOT_PENDING);
        }

        selectedApplication.accept();
        applications.stream()
                .filter(application -> !application.getId().equals(applicationId))
                .filter(JobApplication::isPending)
                .forEach(JobApplication::reject);

        jobPost.match();

        Matching matching = Matching.create(
                jobPost.getId(),
                selectedApplication.getId(),
                request.agreedAmount(),
                request.deadline()
        );

        Matching savedMatching = matchingRepository.save(matching);
        return MatchingCreateResponse.of(savedMatching, jobPost.getRevisionLimit());
    }

    private JobApplication findApplicationOrThrow(Long applicationId) {
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new GeneralException(JobApplicationErrorCode.NOT_FOUND));
    }

    private JobPost findJobPostForUpdateOrThrow(Long jobPostId) {
        return jobPostRepository.findByIdForUpdate(jobPostId)
                .orElseThrow(() -> new GeneralException(JobPostErrorCode.NOT_FOUND));
    }

    private void validateOwner(JobPost jobPost, Long ownerId) {
        if (!jobPost.isOwnedBy(ownerId)) {
            throw new GeneralException(JobPostErrorCode.FORBIDDEN);
        }
    }

    private void validateMatchable(JobPost jobPost) {
        if (!jobPost.isEditable() || matchingRepository.existsByJobPostId(jobPost.getId())) {
            throw new GeneralException(MatchingErrorCode.ALREADY_MATCHED);
        }
    }
}
