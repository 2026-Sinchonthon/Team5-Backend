package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import java.time.OffsetDateTime;

public record JobPostListItemResponse(
        Long jobPostId,
        String title,
        String imageUrl,
        JobPostCategory category,
        Integer budget,
        OffsetDateTime deadline,
        JobPostStatus status,
        OffsetDateTime createdAt
) {

    public static JobPostListItemResponse from(JobPost jobPost) {
        return new JobPostListItemResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getImageUrl(),
                jobPost.getCategory(),
                jobPost.getBudget(),
                jobPost.getDeadline(),
                jobPost.getStatus(),
                jobPost.getCreatedAt()
        );
    }
}
