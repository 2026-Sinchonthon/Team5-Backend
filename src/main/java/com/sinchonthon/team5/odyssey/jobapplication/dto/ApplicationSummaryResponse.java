package com.sinchonthon.team5.odyssey.jobapplication.dto;

import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;

import java.time.OffsetDateTime;

public record ApplicationSummaryResponse(
        Long applicationId,
        JobPostSummary jobPost,
        JobApplicationStatus status,
        OffsetDateTime appliedAt
) {

    public record JobPostSummary(
            Long jobPostId,
            String title,
            String businessName,
            Integer budget,
            OffsetDateTime deadline
    ) {

    }
}
