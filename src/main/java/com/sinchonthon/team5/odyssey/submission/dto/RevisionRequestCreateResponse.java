package com.sinchonthon.team5.odyssey.submission.dto;

import com.sinchonthon.team5.odyssey.submission.domain.RevisionRequest;

import java.time.OffsetDateTime;

public record RevisionRequestCreateResponse(
        Long revisionRequestId,
        Long submissionId,
        String reason,
        Integer revisionCount,
        Integer revisionLimit,
        Integer remainingRevisionCount,
        OffsetDateTime requestedAt
) {
    public static RevisionRequestCreateResponse of(
            RevisionRequest request,
            Integer revisionCount,
            Integer revisionLimit
    ) {
        return new RevisionRequestCreateResponse(
                request.getId(),
                request.getSubmissionId(),
                request.getReason(),
                revisionCount,
                revisionLimit,
                revisionLimit - revisionCount,
                request.getRequestedAt()
        );
    }
}
