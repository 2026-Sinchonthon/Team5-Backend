package com.sinchonthon.team5.odyssey.matching;

import com.sinchonthon.team5.odyssey.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum MatchingErrorCode implements BaseErrorCode {

    APPLICATION_NOT_PENDING(
            HttpStatus.CONFLICT,
            "MATCHING_409_1",
            "대기 중인 지원만 수락할 수 있습니다."
    ),
    ALREADY_MATCHED(
            HttpStatus.CONFLICT,
            "MATCHING_409_2",
            "이미 매칭된 공고입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    MatchingErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
