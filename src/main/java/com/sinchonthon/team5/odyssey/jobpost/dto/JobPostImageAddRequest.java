package com.sinchonthon.team5.odyssey.jobpost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobPostImageAddRequest(

        @NotBlank(message = "이미지 URL은 필수입니다.")
        @Size(max = 1000, message = "이미지 URL은 1000자 이하여야 합니다.")
        String imageUrl
) {
}
