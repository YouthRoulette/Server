package com.youthroulette.server.bucket;

import com.youthroulette.server.bucket.dto.BucketRequest;
import com.youthroulette.server.bucket.dto.BucketResponse;
import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.security.AuthUser;
import com.youthroulette.server.user.User;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BucketService {
    private static final int MAX_BUCKET_COUNT = 8;

    private final BucketItemRepository bucketItemRepository;
    private final AuthUser authUser;

    public BucketService(BucketItemRepository bucketItemRepository, AuthUser authUser) {
        this.bucketItemRepository = bucketItemRepository;
        this.authUser = authUser;
    }

    @Transactional
    public BucketResponse create(BucketRequest request) {
        User user = authUser.get();
        if (bucketItemRepository.countByUser(user) >= MAX_BUCKET_COUNT) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "버킷은 최대 8개까지 등록할 수 있습니다.");
        }
        return BucketResponse.from(bucketItemRepository.save(new BucketItem(user, request.title())));
    }

    @Transactional(readOnly = true)
    public List<BucketResponse> myBuckets(BucketStatus status) {
        User user = authUser.get();

        if (status == null) {
            return bucketItemRepository.findByUserOrderByCreatedAtDesc(user)
                    .stream()
                    .map(BucketResponse::from)
                    .toList();
        }

        return bucketItemRepository.findByUserAndStatus(user, status)
                .stream()
                .map(BucketResponse::from)
                .toList();
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
            throw new ApiException(HttpStatus.BAD_REQUEST, "진행 중인 버킷이 있으면 룰렛을 돌릴 수 없습니다.");
        }
        List<BucketItem> candidates = bucketItemRepository.findByUserAndStatus(user, BucketStatus.TODO);
        if (candidates.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "룰렛 대상 TODO 버킷이 없습니다.");
        }
        return BucketResponse.from(candidates.get(ThreadLocalRandom.current().nextInt(candidates.size())));
    }

    @Transactional
    public BucketResponse start(Long bucketId) {
        User user = authUser.get();
        if (bucketItemRepository.existsByUserAndStatus(user, BucketStatus.IN_PROGRESS)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 진행 중인 버킷이 있습니다.");
        }
        BucketItem bucket = getMyBucket(bucketId);
        if (bucket.getStatus() != BucketStatus.TODO) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "TODO 상태 버킷만 시작할 수 있습니다.");
        }
        bucket.start();
        return BucketResponse.from(bucket);
    }

    @Transactional
    public BucketResponse complete(Long bucketId) {
        BucketItem bucket = getMyBucket(bucketId);
        if (bucket.getStatus() != BucketStatus.IN_PROGRESS) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "진행 중인 버킷만 완료할 수 있습니다.");
        }
        bucket.complete();
        return BucketResponse.from(bucket);
    }

    public BucketItem getMyBucket(Long bucketId) {
        User user = authUser.get();
        BucketItem bucket = bucketItemRepository.findById(bucketId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "버킷을 찾을 수 없습니다."));
        if (!bucket.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "내 버킷만 접근할 수 있습니다.");
        }
        return bucket;
    }
}
