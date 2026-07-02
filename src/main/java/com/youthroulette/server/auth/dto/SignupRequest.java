package com.youthroulette.server.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank @Size(max = 50) String loginId,
    @NotBlank @Size(min = 4, max = 100) String password,
    @NotBlank @Size(max = 50) String nickname
) {
}
