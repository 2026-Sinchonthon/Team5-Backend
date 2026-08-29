package com.sinchonthon.team5.odyssey.global.storage;

import com.sinchonthon.team5.odyssey.global.code.BaseErrorCode;

import org.springframework.http.HttpStatus;

public enum FileStorageErrorCode implements BaseErrorCode {

    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500_1", "파일 업로드에 실패했습니다."),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500_2", "파일 삭제에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    FileStorageErrorCode(HttpStatus status, String code, String message) {
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
