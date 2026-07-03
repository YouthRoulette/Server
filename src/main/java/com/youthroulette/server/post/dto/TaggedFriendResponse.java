package com.youthroulette.server.post.dto;

import com.youthroulette.server.friend.Friend;
import com.youthroulette.server.user.User;

public record TaggedFriendResponse(Long userId, String nickname) {
    public static TaggedFriendResponse from(Friend friend, User postAuthor) {
        User taggedUser = friend.getOther(postAuthor);
        return new TaggedFriendResponse(taggedUser.getId(), taggedUser.getNickname());
    }
}

