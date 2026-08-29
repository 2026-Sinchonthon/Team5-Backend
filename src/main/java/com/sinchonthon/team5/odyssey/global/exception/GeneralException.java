package com.sinchonthon.team5.odyssey.global.exception;

import com.sinchonthon.team5.odyssey.global.code.BaseErrorCode;

public class GeneralException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public GeneralException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }
}
