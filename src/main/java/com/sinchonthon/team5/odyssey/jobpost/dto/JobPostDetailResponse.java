package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record JobPostDetailResponse(
        Long jobPostId,
        Long ownerId,
        String title,
        String description,
        List<JobPostImageResponse> images,
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
                jobPost.getImages().stream().map(JobPostImageResponse::from).toList(),
                jobPost.getCategory(),
                jobPost.getBudget(),
                jobPost.getDeadline(),
                jobPost.getRevisionLimit(),
                jobPost.getStatus(),
                jobPost.getCreatedAt()
        );
    }
}
