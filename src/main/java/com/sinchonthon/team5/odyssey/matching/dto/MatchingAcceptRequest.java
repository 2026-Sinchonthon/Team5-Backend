package com.sinchonthon.team5.odyssey.matching.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record MatchingAcceptRequest(

        @NotNull(message = "합의 금액은 필수입니다.")
        @Positive(message = "합의 금액은 0보다 커야 합니다.")
        Integer agreedAmount,

        @NotNull(message = "작업 기한은 필수입니다.")
        @Future(message = "작업 기한은 현재 시각 이후여야 합니다.")
        OffsetDateTime deadline
) {
}
