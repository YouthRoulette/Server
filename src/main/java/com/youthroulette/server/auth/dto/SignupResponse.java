package com.youthroulette.server.auth.dto;

import com.youthroulette.server.user.User;

public record SignupResponse(Long id, String loginId, String nickname) {
    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getLoginId(), user.getNickname());
    }
}
