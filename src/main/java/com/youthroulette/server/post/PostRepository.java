package com.youthroulette.server.post;

import com.youthroulette.server.bucket.BucketItem;
import com.youthroulette.server.user.User;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByBucketItem(BucketItem bucketItem);
    List<Post> findByUserOrderByCreatedAtDesc(User user);
    List<Post> findByUserInAndVisibilityOrderByCreatedAtDesc(Collection<User> users, PostVisibility visibility);
}
