package com.youthroulette.server.post;

import com.youthroulette.server.friend.Friend;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "posts_friends_tag", uniqueConstraints = @UniqueConstraint(name = "uk_post_friend_tag", columnNames = {"post_id", "friend_id"}))
public class PostFriendTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "posts_friends_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friend friend;

    protected PostFriendTag() {
    }

    public PostFriendTag(Post post, Friend friend) {
        this.post = post;
        this.friend = friend;
    }

    public Long getId() { return id; }
    public Post getPost() { return post; }
    public Friend getFriend() { return friend; }
}
