package com.youthroulette.server.post.dto;

import com.youthroulette.server.post.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePostRequest(
    @NotBlank(message = "imageUrl 누락")
    @Size(max = 500, message = "imageUrl은 최대 500자까지 입력할 수 있습니다.")
    String imageUrl,

    @Size(max = 255, message = "reviewText는 최대 255자까지 입력할 수 있습니다.")
    String reviewText,

    @NotNull(message = "PRIVATE/PUBLIC이 아닌 값")
    PostVisibility visibility,

    List<Long> friendIds
) {
}
