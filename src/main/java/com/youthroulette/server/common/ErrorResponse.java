package com.youthroulette.server.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
    String code,
    String message,
    int status,
    List<FieldErrorResponse> errors
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, errorCode.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.name(), message, errorCode.getStatus().value(), List.of());
    }

    public static ErrorResponse validation(List<FieldErrorResponse> errors) {
        return new ErrorResponse(
            ErrorCode.VALIDATION_ERROR.name(),
            ErrorCode.VALIDATION_ERROR.getMessage(),
            ErrorCode.VALIDATION_ERROR.getStatus().value(),
            errors
        );
    }
}
