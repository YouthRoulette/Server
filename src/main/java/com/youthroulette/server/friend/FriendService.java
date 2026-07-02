package com.youthroulette.server.friend;

import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.friend.dto.FriendListResponse;
import com.youthroulette.server.friend.dto.FriendRequest;
import com.youthroulette.server.friend.dto.FriendResponse;
import com.youthroulette.server.security.AuthUser;
import com.youthroulette.server.user.User;
import com.youthroulette.server.user.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
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
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "친구 요청 대상을 찾을 수 없습니다."));
        if (requester.getId().equals(receiver.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }
        if (friendRepository.existsBetweenUsersWithStatuses(requester, receiver, List.of(FriendStatus.PENDING, FriendStatus.ACCEPTED))) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 친구 요청 또는 친구 관계가 있습니다.");
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
        Friend friend = friendRepository.findByIdAndStatus(friendId, FriendStatus.PENDING)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "대기 중인 친구 요청을 찾을 수 없습니다."));
        if (!friend.getReceiver().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "받은 친구 요청만 수락할 수 있습니다.");
        }
        friend.accept();
        return FriendResponse.from(friend);
    }

    @Transactional
    public FriendResponse reject(Long friendId) {
        User user = authUser.get();
        Friend friend = friendRepository.findByIdAndStatus(friendId, FriendStatus.PENDING)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "대기 중인 친구 요청을 찾을 수 없습니다."));
        if (!friend.getReceiver().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "받은 친구 요청만 거절할 수 있습니다.");
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
                return new FriendListResponse(friend.getId(), other.getId(), other.getNickname());
            }).toList();
    }

    @Transactional
    public void delete(Long friendId) {
        User user = authUser.get();
        Friend friend = friendRepository.findById(friendId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "친구 관계를 찾을 수 없습니다."));
        if (!friend.involves(user)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "내 친구 관계만 삭제할 수 있습니다.");
        }
        friendRepository.delete(friend);
    }

    @Transactional(readOnly = true)
    public List<User> acceptedFriendUsers(User user) {
        return friendRepository.findAllByUserAndStatus(user, FriendStatus.ACCEPTED).stream()
            .map(friend -> friend.getOther(user)).toList();
    }

    @Transactional(readOnly = true)
    public Friend getAcceptedFriend(Long friendId, User user) {
        Friend friend = friendRepository.findByIdAndStatus(friendId, FriendStatus.ACCEPTED)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "친구 관계를 찾을 수 없습니다."));
        if (!friend.involves(user)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "내 친구 관계만 태그할 수 있습니다.");
        }
        return friend;
    }
}
