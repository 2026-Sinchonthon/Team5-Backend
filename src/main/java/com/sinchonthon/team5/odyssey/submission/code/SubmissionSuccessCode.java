package com.sinchonthon.team5.odyssey.submission.code;

import com.sinchonthon.team5.odyssey.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

public enum SubmissionSuccessCode implements BaseSuccessCode {

    CREATED(HttpStatus.CREATED, "SUBMISSION_201", "결과물 제출 성공"),
    HISTORY_READ(HttpStatus.OK, "SUBMISSION_200_1", "제출 내역 조회 성공"),
    REVISION_CREATED(HttpStatus.CREATED, "SUBMISSION_201_1", "수정 요청 성공"),
    APPROVED(HttpStatus.OK, "SUBMISSION_200_2", "결과물 승인 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;

    SubmissionSuccessCode(HttpStatus status, String code, String message) {
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
