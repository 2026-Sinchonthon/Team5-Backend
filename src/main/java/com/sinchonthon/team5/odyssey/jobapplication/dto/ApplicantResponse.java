package com.sinchonthon.team5.odyssey.jobapplication.dto;

import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;

import java.time.OffsetDateTime;

public record ApplicantResponse(
        Long applicantId,
        StudentSummary student,
        String message,
        JobApplicationStatus status,
        OffsetDateTime appliedAt
) {

    public record StudentSummary(
            Long memberId,
            String name,
            String university,
            String major,
            String introduction
    ) {

    }
}
