package com.sinchonthon.team5.odyssey.submission.dto;

import com.sinchonthon.team5.odyssey.matching.domain.Matching;
import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;
import com.sinchonthon.team5.odyssey.submission.domain.Submission;
import com.sinchonthon.team5.odyssey.submission.enums.SubmissionStatus;

import java.time.OffsetDateTime;

public record SubmissionApproveResponse(
        Long submissionId,
        SubmissionStatus submissionStatus,
        Long matchingId,
        MatchingStatus matchingStatus,
        OffsetDateTime completedAt
) {
    public static SubmissionApproveResponse of(
            Submission submission,
            Matching matching
    ) {
        return new SubmissionApproveResponse(
                submission.getId(),
                submission.getStatus(),
                matching.getId(),
                matching.getStatus(),
                matching.getCompletedAt()
        );
    }
}
