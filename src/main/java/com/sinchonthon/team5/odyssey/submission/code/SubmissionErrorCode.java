package com.sinchonthon.team5.odyssey.submission.code;

import com.sinchonthon.team5.odyssey.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum SubmissionErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "SUBMISSION_404", "존재하지 않는 제출물입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "SUBMISSION_403", "해당 제출물에 접근할 권한이 없습니다."),
    FILE_REQUIRED(HttpStatus.BAD_REQUEST, "SUBMISSION_400", "결과물 파일은 한 개 이상 필요합니다."),
    INVALID_STATUS(HttpStatus.CONFLICT, "SUBMISSION_409_1", "현재 상태에서는 처리할 수 없습니다."),
    REVISION_LIMIT_EXCEEDED(
            HttpStatus.CONFLICT,
            "SUBMISSION_409_2",
            "수정 요청 가능 횟수를 모두 사용했습니다."
    ),
    REVISION_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "SUBMISSION_409_3",
            "이미 수정 요청한 제출물입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    SubmissionErrorCode(HttpStatus status, String code, String message) {
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
