package com.youthroulette.server.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank(message = "loginId는 필수입니다.")
    @Size(min = 3, max = 50, message = "아이디는 3~50자여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "3~50자의 영문/숫자/언더스코어만 가능합니다.")
    String loginId,

    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
    String password,

    @NotBlank(message = "nickname은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
    String nickname
) {
}
