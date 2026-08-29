package com.sinchonthon.team5.odyssey.jobapplication.dto;

import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;

import java.time.OffsetDateTime;

public record ApplicationCreateResponse(
        Long applicationId,
        Long jobPostId,
        String imageUrl,
        JobApplicationStatus status,
        OffsetDateTime appliedAt
) {
}
