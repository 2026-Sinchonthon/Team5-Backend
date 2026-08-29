package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

import java.time.OffsetDateTime;

public record JobPostRefineResponse(
        String title,
        String description,
        JobPostCategory category,
        Integer budget,
        String budgetText,
        OffsetDateTime deadline,
        String deadlineText,
        String rawRequest
) {
}
