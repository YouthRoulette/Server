package com.youthroulette.server.post;

import com.youthroulette.server.bucket.BucketItem;
import com.youthroulette.server.bucket.BucketService;
import com.youthroulette.server.bucket.BucketStatus;
import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.common.ErrorCode;
import com.youthroulette.server.friend.Friend;
import com.youthroulette.server.friend.FriendService;
import com.youthroulette.server.post.dto.CreatePostRequest;
import com.youthroulette.server.post.dto.LikeResponse;
import com.youthroulette.server.post.dto.PostResponse;
import com.youthroulette.server.post.dto.TaggedFriendResponse;
import com.youthroulette.server.security.AuthUser;
import com.youthroulette.server.user.User;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
            throw new ApiException(ErrorCode.INVALID_BUCKET_STATUS);
        }
        if (postRepository.existsByBucketItem(bucket)) {
            throw new ApiException(ErrorCode.BUCKET_ALREADY_VERIFIED);
        }
        Post post = postRepository.save(new Post(user, bucket, request.imageUrl(), request.reviewText(), request.visibility()));

        List<TaggedFriendResponse> taggedFriends = new ArrayList<>();
        if (request.friendIds() != null) {
            // 요청에 같은 friendId가 중복으로 들어오면 (post_id, friend_id) 유니크 제약에 걸려 500이 나던 문제 방지
            Set<Long> uniqueFriendIds = new LinkedHashSet<>(request.friendIds());
            for (Long friendId : uniqueFriendIds) {
                Friend friend = friendService.getAcceptedFriend(friendId, user);
                postFriendTagRepository.save(new PostFriendTag(post, friend));
                taggedFriends.add(TaggedFriendResponse.from(friend, user));
            }
        }
        return PostResponse.of(post, 0, false, taggedFriends);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> feed() {
        User user = authUser.get();
        List<User> friends = friendService.acceptedFriendUsers(user);
        if (friends.isEmpty()) {
            return List.of();
        }
        List<Post> posts = postRepository.findByUserInAndVisibilityOrderByCreatedAtDesc(friends, PostVisibility.PUBLIC);
        return toResponses(posts, user);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> myPosts() {
        User user = authUser.get();
        List<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user);
        return toResponses(posts, user);
    }

    @Transactional
    public void delete(Long postId) {
        User user = authUser.get();
        Post post = getPost(postId);
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }
        postRepository.delete(post);
    }

    @Transactional
    public LikeResponse like(Long postId) {
        User user = authUser.get();
        Post post = getPost(postId);
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new ApiException(ErrorCode.ALREADY_LIKED);
        }
        postLikeRepository.save(new PostLike(post, user));
        long count = postLikeRepository.countByPost(post);
        return new LikeResponse(post.getId(), true, count);
    }

    @Transactional
    public LikeResponse unlike(Long postId) {
        User user = authUser.get();
        Post post = getPost(postId);
        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new ApiException(ErrorCode.LIKE_NOT_FOUND));
        postLikeRepository.delete(postLike);
        long count = postLikeRepository.countByPost(post);
        return new LikeResponse(post.getId(), false, count);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
    }

    /** 목록 응답 빌드 시 좋아요 여부/태그된 친구를 게시물별로 매번 쿼리하지 않고 배치로 한 번에 조회 */
    private List<PostResponse> toResponses(List<Post> posts, User currentUser) {
        if (posts.isEmpty()) {
            return List.of();
        }

        Set<Long> likedPostIds = postLikeRepository.findByPostInAndUser(posts, currentUser).stream()
                .map(like -> like.getPost().getId())
                .collect(Collectors.toSet());

        Map<Long, List<PostFriendTag>> tagsByPostId = postFriendTagRepository.findByPostIn(posts).stream()
                .collect(Collectors.groupingBy(tag -> tag.getPost().getId()));

        return posts.stream()
                .map(post -> {
                    long likeCount = postLikeRepository.countByPost(post);
                    boolean likedByMe = likedPostIds.contains(post.getId());
                    List<TaggedFriendResponse> taggedFriends = tagsByPostId
                            .getOrDefault(post.getId(), List.of()).stream()
                            .map(tag -> TaggedFriendResponse.from(tag.getFriend(), post.getUser()))
                            .toList();
                    return PostResponse.of(post, likeCount, likedByMe, taggedFriends);
                })
                .toList();
    }
}
