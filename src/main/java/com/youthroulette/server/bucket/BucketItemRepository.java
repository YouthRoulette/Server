package com.youthroulette.server.bucket;

import com.youthroulette.server.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketItemRepository extends JpaRepository<BucketItem, Long> {
    long countByUser(User user);
    boolean existsByUserAndStatus(User user, BucketStatus status);
    List<BucketItem> findByUserOrderByCreatedAtDesc(User user);
    List<BucketItem> findByUserAndStatus(User user, BucketStatus status);
}
