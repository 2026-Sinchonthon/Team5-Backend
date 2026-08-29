package com.sinchonthon.team5.odyssey.member.code;

import com.sinchonthon.team5.odyssey.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

public enum MemberSuccessCode implements BaseSuccessCode {

    MY_PROFILE_READ(HttpStatus.OK, "MEMBER_200_1", "내 정보 조회 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;

    MemberSuccessCode(HttpStatus status, String code, String message) {
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
