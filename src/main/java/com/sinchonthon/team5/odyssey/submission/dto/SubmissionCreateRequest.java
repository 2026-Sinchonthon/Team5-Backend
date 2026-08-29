package com.sinchonthon.team5.odyssey.submission.dto;

import jakarta.validation.constraints.Size;

public record SubmissionCreateRequest(

        @Size(max = 1000, message = "결과물 설명은 1000자 이하여야 합니다.")
        String description
) {
}
