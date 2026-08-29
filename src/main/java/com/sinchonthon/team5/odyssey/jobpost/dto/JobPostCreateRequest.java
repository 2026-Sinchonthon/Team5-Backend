package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

public record JobPostCreateRequest(

        @NotBlank(message = "공고 제목은 필수입니다.")
        @Size(max = 150, message = "공고 제목은 150자 이하여야 합니다.")
        String title,

        @NotBlank(message = "공고 설명은 필수입니다.")
        String description,

        String rawRequest,

        @Size(max = JobPost.MAX_IMAGE_COUNT, message = "이미지는 최대 10장까지 등록할 수 있습니다.")
        List<
                @NotBlank(message = "이미지 URL은 공백일 수 없습니다.")
                @Size(max = 1000, message = "이미지 URL은 1000자 이하여야 합니다.")
                String
        > imageUrls,

        @NotNull(message = "카테고리는 필수입니다.")
        JobPostCategory category,

        @NotNull(message = "예산은 필수입니다.")
        @Positive(message = "예산은 0보다 커야 합니다.")
        Integer budget,

        @NotNull(message = "마감일은 필수입니다.")
        @Future(message = "마감일은 현재 시각 이후여야 합니다.")
        OffsetDateTime deadline,

        @PositiveOrZero(message = "수정 가능 횟수는 0 이상이어야 합니다.")
        Integer revisionLimit
) {
}
