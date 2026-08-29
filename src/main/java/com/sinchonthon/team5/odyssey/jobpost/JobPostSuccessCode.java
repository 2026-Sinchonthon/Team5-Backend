package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.global.code.BaseSuccessCode;

import org.springframework.http.HttpStatus;

public enum JobPostSuccessCode implements BaseSuccessCode {

    CREATED(HttpStatus.CREATED, "JOBPOST_201", "공고 등록 성공"),
    LIST_FOUND(HttpStatus.OK, "JOBPOST_200_1", "공고 목록 조회 성공"),
    DETAIL_FOUND(HttpStatus.OK, "JOBPOST_200_2", "공고 상세 조회 성공"),
    MY_LIST_FOUND(HttpStatus.OK, "JOBPOST_200_3", "내 공고 목록 조회 성공"),
    UPDATED(HttpStatus.OK, "JOBPOST_200_4", "공고 수정 성공"),
    IMAGE_ADDED(HttpStatus.CREATED, "JOBPOST_201_2", "공고 이미지 추가 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;

    JobPostSuccessCode(HttpStatus status, String code, String message) {
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
