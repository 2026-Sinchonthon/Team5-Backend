package com.sinchonthon.team5.odyssey.member.code;

import com.sinchonthon.team5.odyssey.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum MemberErrorCode implements BaseErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER_409_1", "이미 가입된 이메일입니다."),
    UNSUPPORTED_UNIVERSITY_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER_400_1", "지원하지 않는 대학 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "MEMBER_401_1", "이메일 또는 비밀번호가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    MemberErrorCode(HttpStatus status, String code, String message) {
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
