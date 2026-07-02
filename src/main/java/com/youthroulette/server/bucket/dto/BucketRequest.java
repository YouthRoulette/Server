package com.youthroulette.server.bucket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//버킷 등록
public record BucketRequest(@NotBlank @Size(max = 100) String title) {
}
