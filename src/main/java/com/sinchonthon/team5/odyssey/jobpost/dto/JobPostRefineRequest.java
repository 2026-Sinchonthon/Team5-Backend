package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobPostRefineRequest(

        @NotBlank(message = "원본 요청 내용은 필수입니다.")
        @Size(max = 2000, message = "원본 요청 내용은 2000자 이하여야 합니다.")
        String rawRequest,

        @NotNull(message = "카테고리는 필수입니다.")
        JobPostCategory category
) {
}
