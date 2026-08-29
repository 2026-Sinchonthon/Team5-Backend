package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import java.time.OffsetDateTime;

public record JobPostUpdateResponse(
        Long jobPostId,
        String title,
        String imageUrl,
        Integer budget,
        OffsetDateTime deadline,
        JobPostStatus status
) {

    public static JobPostUpdateResponse from(JobPost jobPost) {
        return new JobPostUpdateResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getImageUrl(),
                jobPost.getBudget(),
                jobPost.getDeadline(),
                jobPost.getStatus()
        );
    }
}
