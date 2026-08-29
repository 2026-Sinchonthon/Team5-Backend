package com.sinchonthon.team5.odyssey.matching.dto;

import com.sinchonthon.team5.odyssey.matching.domain.Matching;
import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;

import java.time.OffsetDateTime;

public record MatchingCreateResponse(
        Long matchingId,
        Long jobPostId,
        Long applicationId,
        Integer agreedAmount,
        OffsetDateTime deadline,
        Integer revisionCount,
        Integer revisionLimit,
        MatchingStatus status,
        OffsetDateTime matchedAt
) {
    public static MatchingCreateResponse of(
            Matching matching,
            Integer revisionLimit
    ) {
        return new MatchingCreateResponse(
                matching.getId(),
                matching.getJobPostId(),
                matching.getApplicationId(),
                matching.getAgreedAmount(),
                matching.getDeadline(),
                matching.getRevisionCount(),
                revisionLimit,
                matching.getStatus(),
                matching.getMatchedAt()
        );
    }
}
