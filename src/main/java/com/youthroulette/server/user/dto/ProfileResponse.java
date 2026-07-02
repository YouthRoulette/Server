package com.youthroulette.server.user.dto;

import com.youthroulette.server.user.User;

public record ProfileResponse(Long userId, Integer emojiIndex, Integer colorIndex) {
    public static ProfileResponse from(User user) {
        return new ProfileResponse(user.getId(), user.getEmojiIndex(), user.getColorIndex());
    }
}
