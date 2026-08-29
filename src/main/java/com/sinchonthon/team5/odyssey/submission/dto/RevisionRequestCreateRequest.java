package com.sinchonthon.team5.odyssey.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RevisionRequestCreateRequest (

        @NotBlank(message = "수정 요청 내용은 필수입니다.")
        @Size(max = 1000, message = "수정 요청 내용은 1000자 이하여야 합니다.")
        String reason
) {
}
