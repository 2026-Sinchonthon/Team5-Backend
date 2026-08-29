package com.sinchonthon.team5.odyssey.jobapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationCreateJsonRequest(

        @NotBlank(message = "이력 설명 및 자기소개는 필수입니다.")
        @Size(max = 1000, message = "이력 설명 및 자기소개는 1000자 이하여야 합니다.")
        String message
) {
}
