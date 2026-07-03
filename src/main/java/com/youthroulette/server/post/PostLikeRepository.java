package com.youthroulette.server.post;

import com.youthroulette.server.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostAndUser(Post post, User user);
    Optional<PostLike> findByPostAndUser(Post post, User user);
    long countByPost(Post post);
    List<PostLike> findByPostInAndUser(Collection<Post> posts, User user);
}
