package com.youthroulette.server.user.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateProfileRequest(@NotNull Integer emojiIndex, @NotNull Integer colorIndex) {
}
