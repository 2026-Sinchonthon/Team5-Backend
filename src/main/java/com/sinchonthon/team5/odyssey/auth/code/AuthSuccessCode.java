package com.sinchonthon.team5.odyssey.auth.code;

import com.sinchonthon.team5.odyssey.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

public enum AuthSuccessCode implements BaseSuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "AUTH_200_1", "로그인에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    AuthSuccessCode(HttpStatus status, String code, String message) {
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
