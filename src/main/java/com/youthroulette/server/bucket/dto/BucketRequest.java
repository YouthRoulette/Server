package com.youthroulette.server.bucket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//버킷 등록
public record BucketRequest(
    @NotBlank(message = "버킷 제목은 필수입니다.")
    @Size(min = 1, max = 100, message = "버킷 제목은 1~100자여야 합니다.")
    String title,

    @NotNull(message = "emojiIndex는 필수입니다.")
    Integer emojiIndex,

    @NotNull(message = "colorIndex는 필수입니다.")
    Integer colorIndex
) {
}
