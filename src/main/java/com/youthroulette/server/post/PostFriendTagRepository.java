package com.youthroulette.server.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostFriendTagRepository extends JpaRepository<PostFriendTag, Long> {
    List<PostFriendTag> findByPost(Post post);
    List<PostFriendTag> findByPostIn(Collection<Post> posts);
}
