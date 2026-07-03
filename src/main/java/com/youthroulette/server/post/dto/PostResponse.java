package com.youthroulette.server.post.dto;

import com.youthroulette.server.post.Post;
import com.youthroulette.server.post.PostVisibility;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
    Long postId,
    Long userId,
    String nickname,
    Long bucketId,
    String bucketTitle,
    String imageUrl,
    String reviewText,
    PostVisibility visibility,
    long likeCount,
    boolean likedByMe,
    List<TaggedFriendResponse> taggedFriends,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PostResponse of(Post post, long likeCount, boolean likedByMe, List<TaggedFriendResponse> taggedFriends) {
        return new PostResponse(post.getId(), post.getUser().getId(), post.getUser().getNickname(),
            post.getBucketItem().getId(), post.getBucketItem().getTitle(), post.getImageUrl(), post.getReviewText(),
                post.getVisibility(), likeCount, likedByMe, taggedFriends, post.getCreatedAt(), post.getUpdatedAt());
    }
}
