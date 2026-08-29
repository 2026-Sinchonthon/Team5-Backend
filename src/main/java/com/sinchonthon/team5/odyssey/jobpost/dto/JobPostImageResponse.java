package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.JobPostImage;

public record JobPostImageResponse(
        Long imageId,
        String imageUrl,
        Integer sortOrder
) {

    public static JobPostImageResponse from(JobPostImage image) {
        return new JobPostImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getSortOrder()
        );
    }
}
