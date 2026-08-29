package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record JobPostDetailResponse(
        Long jobPostId,
        String title,
        String description,
        List<JobPostImageResponse> images,
        JobPostCategory category,
        Integer budget,
        OffsetDateTime deadline,
        Integer revisionLimit,
        JobPostStatus status,
        JobPostOwnerResponse owner,
        OffsetDateTime createdAt
) {

    public static JobPostDetailResponse from(JobPost jobPost, JobPostOwnerResponse owner) {
        return new JobPostDetailResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getDescription(),
                jobPost.getImages().stream().map(JobPostImageResponse::from).toList(),
                jobPost.getCategory(),
                jobPost.getBudget(),
                jobPost.getDeadline(),
                jobPost.getRevisionLimit(),
                jobPost.getStatus(),
                owner,
                jobPost.getCreatedAt()
        );
    }
}
