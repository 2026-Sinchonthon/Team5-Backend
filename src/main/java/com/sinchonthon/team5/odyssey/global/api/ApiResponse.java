package com.sinchonthon.team5.odyssey.global.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sinchonthon.team5.odyssey.global.code.BaseErrorCode;
import com.sinchonthon.team5.odyssey.global.code.BaseSuccessCode;
import com.sinchonthon.team5.odyssey.global.code.GeneralSuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"isSuccess", "code", "message", "result", "error"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final boolean isSuccess;

    @JsonProperty("code")
    private final String code;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("result")
    private final T result;

    @JsonProperty("error")
    private final Object error;

    public static <T> ApiResponse<T> onSuccess(String message, T result) {
        return new ApiResponse<>(
                true,
                GeneralSuccessCode.OK.getCode(),
                message,
                result,
                null
        );
    }

    public static ApiResponse<Void> onSuccess(String message) {
        return new ApiResponse<>(
                true,
                GeneralSuccessCode.OK.getCode(),
                message,
                null,
                null
        );
    }

    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode successCode, T result) {
        return new ApiResponse<>(
                true,
                successCode.getCode(),
                successCode.getMessage(),
                result,
                null
        );
    }

    public static ApiResponse<Void> onSuccess(BaseSuccessCode successCode) {
        return onSuccess(successCode, null);
    }

    public static ApiResponse<Void> onFailure(BaseErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                null
        );
    }

    public static ApiResponse<Void> onFailure(BaseErrorCode errorCode, Object error) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                error
        );
    }
}
