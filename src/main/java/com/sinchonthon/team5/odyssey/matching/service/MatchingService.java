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
import com.sinchonthon.team5.odyssey.matching.dto.MatchingDetailResponse;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingSummaryResponse;
import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;
import com.sinchonthon.team5.odyssey.matching.repository.MatchingDetailProjection;
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

    public List<MatchingSummaryResponse> getMyMatchings(
            Long memberId,
            MatchingStatus status
    ) {
        return matchingRepository
                .findSummariesByMemberIdAndStatus(memberId, status)
                .stream()
                .map(MatchingSummaryResponse::from)
                .toList();
    }

    public MatchingDetailResponse getDetail(
            Long memberId,
            Long matchingId
    ) {
        MatchingDetailProjection projection = matchingRepository
                .findDetailById(matchingId)
                .orElseThrow(() -> new GeneralException(MatchingErrorCode.NOT_FOUND));

        validateParticipant(projection, memberId);

        return MatchingDetailResponse.of(
                projection,
                resolveUniversityName(projection.getUniversityId())
        );
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

    private void validateParticipant(
            MatchingDetailProjection projection,
            Long memberId
    ) {
        boolean isOwner = projection.getOwnerId().equals(memberId);
        boolean isStudent = projection.getStudentId().equals(memberId);

        if (!isOwner && !isStudent) {
            throw new GeneralException(MatchingErrorCode.FORBIDDEN);
        }
    }

    private String resolveUniversityName(Long universityId) {
        if (universityId == null) {
            return null;
        }

        return switch (universityId.intValue()) {
            case 1 -> "연세대학교";
            case 2 -> "이화여자대학교";
            case 3 -> "서강대학교";
            default -> "알 수 없는 대학교";
        };
    }
}
