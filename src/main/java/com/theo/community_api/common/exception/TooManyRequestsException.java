package com.theo.community_api.common.exception;

import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends BusinessException {
    // 이후에 사용예정 : 로그인 일정시간동안 여러번 요청 시
    public TooManyRequestsException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS);
    }
}