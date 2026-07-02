package com.youthroulette.server.image.dto;

import jakarta.validation.constraints.NotBlank;

public record PresignedUrlRequest(@NotBlank(message = "fileName은 필수입니다.") String fileName) {
}
