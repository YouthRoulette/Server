package com.youthroulette.server.post.dto;

import com.youthroulette.server.post.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePostRequest(
    @NotBlank @Size(max = 500) String imageUrl,
    @Size(max = 255) String reviewText,
    @NotNull PostVisibility visibility,
    List<Long> friendIds
) {
}
