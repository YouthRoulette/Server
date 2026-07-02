package com.youthroulette.server.post;

import com.youthroulette.server.bucket.BucketItem;
import com.youthroulette.server.bucket.BucketService;
import com.youthroulette.server.bucket.BucketStatus;
import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.friend.Friend;
import com.youthroulette.server.friend.FriendService;
import com.youthroulette.server.post.dto.CreatePostRequest;
import com.youthroulette.server.post.dto.LikeCountResponse;
import com.youthroulette.server.post.dto.PostResponse;
import com.youthroulette.server.security.AuthUser;
import com.youthroulette.server.user.User;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostFriendTagRepository postFriendTagRepository;
    private final BucketService bucketService;
    private final FriendService friendService;
    private final AuthUser authUser;

    public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository,
        PostFriendTagRepository postFriendTagRepository, BucketService bucketService, FriendService friendService, AuthUser authUser) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postFriendTagRepository = postFriendTagRepository;
        this.bucketService = bucketService;
        this.friendService = friendService;
        this.authUser = authUser;
    }

    @Transactional
    public PostResponse create(Long bucketId, CreatePostRequest request) {
        User user = authUser.get();
        BucketItem bucket = bucketService.getMyBucket(bucketId);
        if (bucket.getStatus() != BucketStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "COMPLETED 상태 버킷만 인증글을 작성할 수 있습니다.");
        }
        Post post = postRepository.save(new Post(user, bucket, request.imageUrl(), request.reviewText(), request.visibility()));
        if (request.friendIds() != null) {
            for (Long friendId : request.friendIds()) {
                Friend friend = friendService.getAcceptedFriend(friendId, user);
                postFriendTagRepository.save(new PostFriendTag(post, friend));
            }
        }
        return PostResponse.from(post, 0);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> feed() {
        User user = authUser.get();
        List<User> friends = friendService.acceptedFriendUsers(user);
        if (friends.isEmpty()) {
            return List.of();
        }
        return postRepository.findByUserInAndVisibilityOrderByCreatedAtDesc(friends, PostVisibility.PUBLIC).stream()
            .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> myPosts() {
        return postRepository.findByUserOrderByCreatedAtDesc(authUser.get()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void delete(Long postId) {
        User user = authUser.get();
        Post post = getPost(postId);
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "내 인증글만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
    }

    @Transactional
    public void like(Long postId) {
        User user = authUser.get();
        Post post = getPost(postId);
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            return;
        }
        postLikeRepository.save(new PostLike(post, user));
    }

    @Transactional
    public void unlike(Long postId) {
        User user = authUser.get();
        Post post = getPost(postId);
        postLikeRepository.findByPostAndUser(post, user).ifPresent(postLikeRepository::delete);
    }

    @Transactional(readOnly = true)
    public LikeCountResponse likeCount(Long postId) {
        Post post = getPost(postId);
        return new LikeCountResponse(post.getId(), postLikeRepository.countByPost(post));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "인증글을 찾을 수 없습니다."));
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.from(post, postLikeRepository.countByPost(post));
    }
}
