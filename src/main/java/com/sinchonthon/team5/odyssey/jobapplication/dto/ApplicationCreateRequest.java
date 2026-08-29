package com.sinchonthon.team5.odyssey.jobapplication.dto;

import jakarta.validation.constraints.Size;

public record ApplicationCreateRequest (

        @Size(max = 1000, message = "지원 메시지는 1000자 이하여야 합니다.")
        String message
) {
}
