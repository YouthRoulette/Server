package com.youthroulette.server.friend.dto;

import com.youthroulette.server.friend.Friend;
import com.youthroulette.server.friend.FriendStatus;

import java.time.LocalDateTime;

public record FriendResponse(
    Long friendId,
    Long requesterId,
    String requesterNickname,
    Long receiverId,
    String receiverNickname,
    FriendStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static FriendResponse from(Friend friend) {
        return new FriendResponse(friend.getId(), friend.getRequester().getId(), friend.getRequester().getNickname(),
            friend.getReceiver().getId(), friend.getReceiver().getNickname(), friend.getStatus(),
            friend.getCreatedAt(), friend.getUpdatedAt());
    }
}
