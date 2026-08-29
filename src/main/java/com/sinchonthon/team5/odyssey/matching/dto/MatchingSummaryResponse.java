package com.sinchonthon.team5.odyssey.matching.dto;

import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;
import com.sinchonthon.team5.odyssey.matching.repository.MatchingSummaryProjection;

import java.time.OffsetDateTime;

public record MatchingSummaryResponse(
        Long matchingId,
        JobPostSummary jobPost,
        Integer agreedAmount,
        OffsetDateTime deadline,
        Integer revisionCount,
        Integer revisionLimit,
        MatchingStatus status
) {
    public static MatchingSummaryResponse from(MatchingSummaryProjection projection) {
        return new MatchingSummaryResponse(
                projection.getMatchingId(),
                new JobPostSummary(projection.getJobPostId(), projection.getTitle()),
                projection.getAgreedAmount(),
                projection.getDeadline(),
                projection.getRevisionCount(),
                projection.getRevisionLimit(),
                projection.getStatus()
        );
    }

    public record JobPostSummary(
            Long jobPostId,
            String title
    ) {
    }
}
