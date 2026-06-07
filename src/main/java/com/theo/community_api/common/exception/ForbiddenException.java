package com.theo.community_api.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message){
        super(message, HttpStatus.FORBIDDEN);
    }
}
