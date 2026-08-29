package com.sinchonthon.team5.odyssey.jobapplication;

import com.sinchonthon.team5.odyssey.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

public enum JobApplicationSuccessCode implements BaseSuccessCode {

    CREATED(
            HttpStatus.CREATED,
            "APPLICATION_201",
            "공고 지원 성공"
    ),
    CANCELED(
            HttpStatus.OK,
            "APPLICATION_200_1",
            "지원 취소 성공"
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    JobApplicationSuccessCode(
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
