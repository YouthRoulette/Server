package com.youthroulette.server.user.dto;

import com.youthroulette.server.user.User;

import java.time.LocalDateTime;

public record UserResponse(Long userId, String loginId, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getLoginId(), user.getNickname(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
