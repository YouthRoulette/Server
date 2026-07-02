package com.youthroulette.server.image.dto;

import jakarta.validation.constraints.NotBlank;

public record PresignedUrlRequest(@NotBlank String fileName) {
}
