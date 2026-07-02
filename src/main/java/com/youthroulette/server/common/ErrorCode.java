package com.youthroulette.server.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    LOGINID_DUPLICATED(HttpStatus.CONFLICT, "이미 사용중인 아이디입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    BUCKET_NOT_FOUND(HttpStatus.NOT_FOUND, "버킷을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다."),
    NO_BUCKET_ITEMS(HttpStatus.NOT_FOUND, "도전 가능한 버킷이 없습니다."),
    ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "이미 도전 중인 버킷이 있습니다."),
    BUCKET_ALREADY_STARTED(HttpStatus.CONFLICT, "이미 도전 중인 버킷입니다."),
    BUCKET_ALREADY_VERIFIED(HttpStatus.CONFLICT, "이미 인증된 버킷입니다."),
    INVALID_FRIENDSHIP(HttpStatus.BAD_REQUEST, "유효하지 않은 친구 관계가 포함되어 있습니다."),
    INVALID_BUCKET_STATUS(HttpStatus.CONFLICT, "완료 상태의 버킷만 인증할 수 있습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "인증글을 찾을 수 없습니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시글입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 누른 기록이 없습니다."),
    FRIEND_REQUEST_DUPLICATED(HttpStatus.CONFLICT, "이미 친구 요청을 보냈습니다."),
    SELF_FRIEND_REQUEST(HttpStatus.BAD_REQUEST, "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."),
    ALREADY_ACCEPTED(HttpStatus.CONFLICT, "이미 수락된 친구 요청입니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다."),

    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),
    BUSINESS_RULE_VIOLATION(HttpStatus.BAD_REQUEST, "요청을 처리할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorCode from(HttpStatus status) {
        return switch (status) {
            case UNAUTHORIZED -> UNAUTHORIZED;
            case FORBIDDEN -> ACCESS_DENIED;
            case NOT_FOUND -> NOT_FOUND;
            case CONFLICT -> DUPLICATE_RESOURCE;
            case BAD_REQUEST -> BUSINESS_RULE_VIOLATION;
            default -> INTERNAL_SERVER_ERROR;
        };
    }
}
