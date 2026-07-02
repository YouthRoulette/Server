package com.youthroulette.server.friend.dto;

import jakarta.validation.constraints.NotBlank;

public record FriendRequest(@NotBlank(message = "loginId는 필수입니다.") String loginId) {
}
