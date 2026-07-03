package com.youthroulette.server.bucket.dto;

import com.youthroulette.server.bucket.BucketItem;
import com.youthroulette.server.bucket.BucketStatus;

import java.time.LocalDateTime;

//버킷 조회
public record BucketResponse(
    Long bucketId,
    String title,
    Integer emojiIndex,
    Integer colorIndex,
    BucketStatus status,
    LocalDateTime createdAt,
    LocalDateTime startedAt,
    LocalDateTime completedAt
) {
    public static BucketResponse from(BucketItem bucket) {
        return new BucketResponse(bucket.getId(), bucket.getTitle(), bucket.getEmojiIndex(), bucket.getColorIndex(),
                bucket.getStatus(), bucket.getCreatedAt(), bucket.getStartedAt(), bucket.getCompletedAt());
    }
}
