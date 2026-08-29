package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import java.time.OffsetDateTime;

public record JobPostCreateResponse(
        Long jobPostId,
        String title,
        JobPostCategory category,
        Integer budget,
        OffsetDateTime deadline,
        JobPostStatus status,
        OffsetDateTime createdAt
) {

    public static JobPostCreateResponse from(JobPost jobPost) {
        return new JobPostCreateResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getCategory(),
                jobPost.getBudget(),
                jobPost.getDeadline(),
                jobPost.getStatus(),
                jobPost.getCreatedAt()
        );
    }
}
