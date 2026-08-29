package com.sinchonthon.team5.odyssey.jobpost.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record JobPostUpdateRequest(

        @Size(max = 150, message = "공고 제목은 150자 이하여야 합니다.")
        String title,

        String description,

        @Size(max = 1000, message = "이미지 URL은 1000자 이하여야 합니다.")
        String imageUrl,

        @Positive(message = "예산은 0보다 커야 합니다.")
        Integer budget,

        @Future(message = "마감일은 현재 시각 이후여야 합니다.")
        OffsetDateTime deadline
) {
}
