package com.youthroulette.server.friend;

import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.common.ErrorCode;
import com.youthroulette.server.friend.dto.FriendListResponse;
import com.youthroulette.server.friend.dto.FriendRequest;
import com.youthroulette.server.friend.dto.FriendResponse;
import com.youthroulette.server.security.AuthUser;
import com.youthroulette.server.user.User;
import com.youthroulette.server.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final AuthUser authUser;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository, AuthUser authUser) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.authUser = authUser;
    }

    @Transactional
    public FriendResponse request(FriendRequest request) {
        User requester = authUser.get();
        User receiver = userRepository.findByLoginId(request.loginId())
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (requester.getId().equals(receiver.getId())) {
            throw new ApiException(ErrorCode.SELF_FRIEND_REQUEST);
        }
        if (friendRepository.existsBetweenUsersWithStatuses(requester, receiver, List.of(FriendStatus.PENDING))) {
            throw new ApiException(ErrorCode.FRIEND_REQUEST_DUPLICATED);
        }
        if (friendRepository.existsBetweenUsersWithStatuses(requester, receiver, List.of(FriendStatus.ACCEPTED))) {
            throw new ApiException(ErrorCode.ALREADY_ACCEPTED);
        }
        return FriendResponse.from(friendRepository.save(new Friend(requester, receiver)));
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> receivedRequests() {
        return friendRepository.findByReceiverAndStatusOrderByCreatedAtDesc(authUser.get(), FriendStatus.PENDING)
            .stream().map(FriendResponse::from).toList();
    }

    @Transactional
    public FriendResponse accept(Long friendId) {
        User user = authUser.get();
        Friend acceptedFriend = friendRepository.findByIdAndStatus(friendId, FriendStatus.ACCEPTED).orElse(null);
        if (acceptedFriend != null) {
            throw new ApiException(ErrorCode.ALREADY_ACCEPTED);
        }
        Friend friend = friendRepository.findByIdAndStatus(friendId, FriendStatus.PENDING)
            .orElseThrow(() -> new ApiException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
        if (!friend.getReceiver().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.ACCESS_DENIED, "수락 권한이 없습니다.");
        }
        friend.accept();
        return FriendResponse.from(friend);
    }

    @Transactional
    public FriendResponse reject(Long friendId) {
        User user = authUser.get();
        Friend friend = friendRepository.findByIdAndStatus(friendId, FriendStatus.PENDING)
            .orElseThrow(() -> new ApiException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
        if (!friend.getReceiver().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.ACCESS_DENIED, "거절 권한이 없습니다.");
        }
        friend.reject();
        return FriendResponse.from(friend);
    }

    @Transactional(readOnly = true)
    public List<FriendListResponse> friends() {
        User user = authUser.get();
        return friendRepository.findAllByUserAndStatus(user, FriendStatus.ACCEPTED).stream()
            .map(friend -> {
                User other = friend.getOther(user);
                return new FriendListResponse(friend.getId(),
                        other.getId(),
                        other.getNickname(),
                        other.getEmojiIndex(),
                        other.getColorIndex());
            }).toList();
    }

    @Transactional(readOnly = true)
    public List<User> acceptedFriendUsers(User user) {
        return friendRepository.findAllByUserAndStatus(user, FriendStatus.ACCEPTED).stream()
            .map(friend -> friend.getOther(user)).toList();
    }

    @Transactional(readOnly = true)
    public Friend getAcceptedFriend(Long friendId, User user) {
        Friend friend = friendRepository.findByIdAndStatus(friendId, FriendStatus.ACCEPTED)
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_FRIENDSHIP));
        if (!friend.involves(user)) {
            throw new ApiException(ErrorCode.INVALID_FRIENDSHIP);
        }
        return friend;
    }
}
