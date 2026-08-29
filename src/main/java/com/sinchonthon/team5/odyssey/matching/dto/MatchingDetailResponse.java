package com.sinchonthon.team5.odyssey.matching.dto;

import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;
import com.sinchonthon.team5.odyssey.matching.repository.MatchingDetailProjection;

import java.time.OffsetDateTime;

public record MatchingDetailResponse(
        Long matchingId,
        JobPostDetail jobPost,
        OwnerSummary owner,
        StudentSummary student,
        Integer agreedAmount,
        OffsetDateTime deadline,
        Integer revisionCount,
        Integer revisionLimit,
        MatchingStatus status
) {
    public static MatchingDetailResponse of(
            MatchingDetailProjection projection,
            String university
    ) {
        return new MatchingDetailResponse(
                projection.getMatchingId(),
                new JobPostDetail(
                        projection.getJobPostId(),
                        projection.getTitle(),
                        projection.getDescription()
                ),
                new OwnerSummary(
                        projection.getOwnerId(),
                        projection.getOwnerName(),
                        projection.getBusinessName()
                ),
                new StudentSummary(
                        projection.getStudentId(),
                        projection.getStudentName(),
                        university,
                        projection.getMajor()
                ),
                projection.getAgreedAmount(),
                projection.getDeadline(),
                projection.getRevisionCount(),
                projection.getRevisionLimit(),
                projection.getStatus()
        );
    }

    public record JobPostDetail(
            Long jobPostId,
            String title,
            String description
    ) {
    }

    public record OwnerSummary(
            Long memberId,
            String name,
            String businessName
    ) {
    }

    public record StudentSummary(
            Long memberId,
            String name,
            String university,
            String major
    ) {
    }
}
