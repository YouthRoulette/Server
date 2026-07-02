package com.youthroulette.server.image.dto;

public record PresignedUrlResponse(String uploadUrl, String imageUrl) {
}
