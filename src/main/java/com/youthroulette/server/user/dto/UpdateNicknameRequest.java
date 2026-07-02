package com.youthroulette.server.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNicknameRequest(@NotBlank @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.") String nickname) {
}
