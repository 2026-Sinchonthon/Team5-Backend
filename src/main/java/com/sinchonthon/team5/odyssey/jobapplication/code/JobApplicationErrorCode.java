package com.sinchonthon.team5.odyssey.jobapplication;

import com.sinchonthon.team5.odyssey.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum JobApplicationErrorCode implements BaseErrorCode {

    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "APPLICATION_404",
            "존재하지 않는 지원입니다."
    ),
    FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "APPLICATION_403",
            "본인이 작성한 지원만 처리할 수 있습니다."
    ),
    DUPLICATE_APPLICATION(
            HttpStatus.CONFLICT,
            "APPLICATION_409_1",
            "이미 지원한 공고입니다."
    ),
    JOB_POST_NOT_OPEN(
            HttpStatus.CONFLICT,
            "APPLICATION_409_2",
            "지원 가능한 상태의 공고가 아닙니다."
    ),
    NOT_CANCELABLE(
            HttpStatus.CONFLICT,
            "APPLICATION_409_3",
            "대기 중인 지원만 취소할 수 있습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    JobApplicationErrorCode(
            HttpStatus status,
            String code,
            String message
    ) {
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