package com.youthroulette.server.bucket;

import com.youthroulette.server.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BucketItemRepository extends JpaRepository<BucketItem, Long> {
    long countByUser(User user);
    boolean existsByUserAndStatus(User user, BucketStatus status);
    List<BucketItem> findByUserOrderByCreatedAtDesc(User user);
    List<BucketItem> findByUserAndStatus(User user, BucketStatus status);

    // COMPLETED 상태인데 아직 인증 게시물이 없는 버킷만
    @Query("""
        select b from BucketItem b
        where b.user = :user
          and b.status = com.youthroulette.server.bucket.BucketStatus.COMPLETED
          and not exists (select p from Post p where p.bucketItem = b)
        order by b.completedAt desc
        """)
    List<BucketItem> findCompletedAndUnverifiedByUser(@Param("user") User user);

    // COMPLETED 상태이면서 이미 인증 게시물이 있는 버킷만
    @Query("""
        select b from BucketItem b
        where b.user = :user
          and b.status = com.youthroulette.server.bucket.BucketStatus.COMPLETED
          and exists (select p from Post p where p.bucketItem = b)
        order by b.completedAt desc
        """)
    List<BucketItem> findCompletedAndVerifiedByUser(@Param("user") User user);
}
