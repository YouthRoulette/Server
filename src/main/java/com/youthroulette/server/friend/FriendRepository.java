package com.youthroulette.server.friend;

import com.youthroulette.server.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("""
        select count(f) > 0 from Friend f
        where ((f.requester = :user1 and f.receiver = :user2) or (f.requester = :user2 and f.receiver = :user1))
          and f.status in :statuses
        """)
    boolean existsBetweenUsersWithStatuses(@Param("user1") User user1, @Param("user2") User user2, @Param("statuses") Collection<FriendStatus> statuses);

    List<Friend> findByReceiverAndStatusOrderByCreatedAtDesc(User receiver, FriendStatus status);

    @Query("""
        select f from Friend f
        where (f.requester = :user or f.receiver = :user) and f.status = :status
        order by f.createdAt desc
        """)
    List<Friend> findAllByUserAndStatus(@Param("user") User user, @Param("status") FriendStatus status);

    Optional<Friend> findByIdAndStatus(Long id, FriendStatus status);
}
