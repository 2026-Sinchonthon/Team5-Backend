package com.sinchonthon.team5.odyssey.matching;

import com.sinchonthon.team5.odyssey.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

public enum MatchingSuccessCode implements BaseSuccessCode {

    CREATED(HttpStatus.CREATED, "MATCHING_201", "매칭 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;

    MatchingSuccessCode(HttpStatus status, String code, String message) {
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
