package com.youthroulette.server.post.dto;

public record LikeResponse(Long postId, boolean likedByMe, long likeCount) {
}
