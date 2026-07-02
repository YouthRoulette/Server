package com.youthroulette.server.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank
        @Size(min = 3, max = 50, message = "3~50자의 영문/숫자/언더스코어만 가능합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "3~50자의 영문/숫자/언더스코어만 가능합니다.")
        String loginId,

        @NotBlank
        @Size(min = 8, max = 100, message = "8자 이상이어야 합니다.")
        String password,

        @NotBlank
        @Size(min = 2, max = 20, message = "2자 이상 20자 이하이어야 합니다.")
        String nickname
) {
}
