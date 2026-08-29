package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.global.code.BaseErrorCode;

import org.springframework.http.HttpStatus;

public enum JobPostErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "JOBPOST_404", "존재하지 않는 공고입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "JOBPOST_403", "본인이 등록한 공고만 처리할 수 있습니다."),
    NOT_EDITABLE(HttpStatus.CONFLICT, "JOBPOST_409", "OPEN 상태의 공고만 수정하거나 취소할 수 있습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "JOBPOST_404_2", "존재하지 않는 공고 이미지입니다."),
    IMAGE_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "JOBPOST_409_2", "이미지는 최대 10장까지 등록할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    JobPostErrorCode(HttpStatus status, String code, String message) {
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
