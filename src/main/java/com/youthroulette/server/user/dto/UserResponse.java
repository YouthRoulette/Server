package com.youthroulette.server.user.dto;

import com.youthroulette.server.user.User;

import java.time.LocalDateTime;

public record UserResponse(Long userId, String loginId, String nickname, Integer emojiIndex, Integer colorIndex, int challengedCount, int completedCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getLoginId(), user.getNickname(), user.getEmojiIndex(), user.getColorIndex(), user.getChallengedCount(), user.getCompletedCount(),user.getCreatedAt(), user.getUpdatedAt());
    }
}
