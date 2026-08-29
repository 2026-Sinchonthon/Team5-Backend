package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import java.time.OffsetDateTime;

public record JobPostDetailResponse(
        Long jobPostId,
        Long ownerId,
        String title,
        String description,
        JobPostCategory category,
        Integer budget,
        OffsetDateTime deadline,
        Integer revisionLimit,
        JobPostStatus status,
        OffsetDateTime createdAt
) {

    public static JobPostDetailResponse from(JobPost jobPost) {
        return new JobPostDetailResponse(
                jobPost.getId(),
                jobPost.getOwnerId(),
                jobPost.getTitle(),
                jobPost.getDescription(),
                jobPost.getCategory(),
                jobPost.getBudget(),
                jobPost.getDeadline(),
                jobPost.getRevisionLimit(),
                jobPost.getStatus(),
                jobPost.getCreatedAt()
        );
    }
}
