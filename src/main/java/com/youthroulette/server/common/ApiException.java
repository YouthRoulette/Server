package com.youthroulette.server.common;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(HttpStatus status, String message) {
        this(ErrorCode.from(status), message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }
}
