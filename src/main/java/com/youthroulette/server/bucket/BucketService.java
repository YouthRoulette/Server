package com.youthroulette.server.bucket;

import com.youthroulette.server.bucket.dto.BucketRequest;
import com.youthroulette.server.bucket.dto.BucketResponse;
import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.common.ErrorCode;
import com.youthroulette.server.post.PostRepository;
import com.youthroulette.server.security.AuthUser;
import com.youthroulette.server.user.User;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BucketService {
    private static final int MAX_BUCKET_COUNT = 8;

    private final BucketItemRepository bucketItemRepository;
    private final PostRepository postRepository;
    private final AuthUser authUser;

    public BucketService(BucketItemRepository bucketItemRepository, PostRepository postRepository, AuthUser authUser) {
        this.bucketItemRepository = bucketItemRepository;
        this.postRepository = postRepository;
        this.authUser = authUser;
    }

    @Transactional
    public BucketResponse create(BucketRequest request) {
        User user = authUser.get();
        if (bucketItemRepository.countByUser(user) >= MAX_BUCKET_COUNT) {
            throw new ApiException(ErrorCode.BUSINESS_RULE_VIOLATION, "버킷은 최대 8개까지 등록할 수 있습니다.");
        }
        return BucketResponse.from(bucketItemRepository.save(new BucketItem(user, request.title())));
    }

    @Transactional(readOnly = true)
    public List<BucketResponse> myBuckets(BucketStatus status) {
        User user = authUser.get();
        if (status == null) {
            return bucketItemRepository.findByUserOrderByCreatedAtDesc(user).stream().map(BucketResponse::from).toList();
        }
        return bucketItemRepository.findByUserAndStatus(user, status).stream().map(BucketResponse::from).toList();
    }

    @Transactional
    public void delete(Long bucketId) {
        BucketItem bucket = getMyBucket(bucketId);
        bucketItemRepository.delete(bucket);
    }

    @Transactional(readOnly = true)
    public BucketResponse roulette() {
        User user = authUser.get();
        if (bucketItemRepository.existsByUserAndStatus(user, BucketStatus.IN_PROGRESS)) {
            throw new ApiException(ErrorCode.ALREADY_IN_PROGRESS);
        }
        List<BucketItem> candidates = bucketItemRepository.findByUserAndStatus(user, BucketStatus.TODO);
        if (candidates.isEmpty()) {
            throw new ApiException(ErrorCode.NO_BUCKET_ITEMS);
        }
        return BucketResponse.from(candidates.get(ThreadLocalRandom.current().nextInt(candidates.size())));
    }

    @Transactional
    public BucketResponse start(Long bucketId) {
        User user = authUser.get();
        BucketItem bucket = getMyBucket(bucketId);
        if (bucket.getStatus() == BucketStatus.IN_PROGRESS) {
            throw new ApiException(ErrorCode.BUCKET_ALREADY_STARTED);
        }
        if (bucketItemRepository.existsByUserAndStatus(user, BucketStatus.IN_PROGRESS)) {
            throw new ApiException(ErrorCode.ALREADY_IN_PROGRESS);
        }
        if (bucket.getStatus() == BucketStatus.COMPLETED) {
            throw new ApiException(ErrorCode.BUCKET_ALREADY_VERIFIED);
        }
        bucket.start();
        return BucketResponse.from(bucket);
    }

    @Transactional
    public BucketResponse complete(Long bucketId) {
        BucketItem bucket = getMyBucket(bucketId);
        if (bucket.getStatus() == BucketStatus.COMPLETED) {
            throw new ApiException(ErrorCode.BUCKET_ALREADY_VERIFIED);
        }
        if (bucket.getStatus() != BucketStatus.IN_PROGRESS) {
            throw new ApiException(ErrorCode.INVALID_BUCKET_STATUS, "도전 중인 버킷만 완료할 수 있습니다.");
        }
        bucket.complete();
        return BucketResponse.from(bucket);
    }

    @Transactional
    public BucketResponse incomplete(Long bucketId) {
        BucketItem bucket = getMyBucket(bucketId);
        if (postRepository.existsByBucketItem(bucket)) {
            throw new ApiException(ErrorCode.BUCKET_ALREADY_VERIFIED);
        }
        if (bucket.getStatus() == BucketStatus.COMPLETED) {
            bucket.incomplete();
        }
        return BucketResponse.from(bucket);
    }

    public BucketItem getMyBucket(Long bucketId) {
        User user = authUser.get();
        BucketItem bucket = bucketItemRepository.findById(bucketId)
            .orElseThrow(() -> new ApiException(ErrorCode.BUCKET_NOT_FOUND));
        if (!bucket.getUser().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }
        return bucket;
    }
}
