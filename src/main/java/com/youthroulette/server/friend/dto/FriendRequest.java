package com.youthroulette.server.friend.dto;

import jakarta.validation.constraints.NotNull;

public record FriendRequest(@NotNull String loginId) {
}
