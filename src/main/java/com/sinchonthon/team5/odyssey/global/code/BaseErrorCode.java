package com.sinchonthon.team5.odyssey.global.code;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getStatus();

    String getCode();

    String getMessage();
}
