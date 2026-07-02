package com.youthroulette.server.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "challenged_count", nullable = false)
    private int challengedCount = 0;

    @Column(name = "completed_count", nullable = false)
    private int completedCount = 0;

    @Column(name = "emoji_index", nullable = false)
    private int emojiIndex = 0;

    @Column(name = "color_index", nullable = false)
    private int colorIndex = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected User() {
    }

    public User(String loginId, String password, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getLoginId() { return loginId; }
    public String getPassword() { return password; }
    public String getNickname() { return nickname; }
    public int getChallengedCount() { return challengedCount; }
    public int getCompletedCount() { return completedCount; }
    public Integer getEmojiIndex() { return emojiIndex; }
    public Integer getColorIndex() { return colorIndex; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(int emojiIndex, int colorIndex) {
        this.emojiIndex = emojiIndex;
        this.colorIndex = colorIndex;
    }

    //도전 시작(POST /api/buckets/{bucketId}/start) 성공 시 bucket 도메인 서비스에서 호출 — 누적 카운트라 감소 로직 없음
    public void increaseChallengedCount() {
        this.challengedCount++;
    }

    //버킷 완료 처리(POST /api/buckets/{bucketId}/complete) 성공 시 bucket 도메인 서비스에서 호출
    public void increaseCompletedCount() {
        this.completedCount++;
    }

    //버킷 완료 취소(POST /api/buckets/{bucketId}/not) 시 bucket 도메인 서비스에서 호출
    public void decreaseCompletedCount() {
        if (this.completedCount > 0) {
            this.completedCount--;
        }
    }
}
