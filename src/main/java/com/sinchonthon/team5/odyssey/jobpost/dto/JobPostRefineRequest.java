package com.sinchonthon.team5.odyssey.jobpost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobPostRefineRequest(

        @NotBlank(message = "원본 요청 내용은 필수입니다.")
        @Size(max = 2000, message = "원본 요청 내용은 2000자 이하여야 합니다.")
        String rawRequest
) {
}
