package com.youthroulette.server.friend;

import com.youthroulette.server.post.PostFriendTag;
import com.youthroulette.server.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "friends", uniqueConstraints = @UniqueConstraint(name = "uk_friends_pair", columnNames = {"requester_id", "receiver_id"}))
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FriendStatus status = FriendStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFriendTag> postFriendTags = new ArrayList<>();

    protected Friend() {
    }

    public Friend(User requester, User receiver) {
        this.requester = requester;
        this.receiver = receiver;
    }

    @PrePersist
    void prePersist() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    void preUpdate() { updatedAt = LocalDateTime.now(); }

    public void accept() { status = FriendStatus.ACCEPTED; }
    public void reject() { status = FriendStatus.REJECTED; }
    public boolean involves(User user) { return requester.getId().equals(user.getId()) || receiver.getId().equals(user.getId()); }
    public User getOther(User user) { return requester.getId().equals(user.getId()) ? receiver : requester; }

    public Long getId() { return id; }
    public User getRequester() { return requester; }
    public User getReceiver() { return receiver; }
    public FriendStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
