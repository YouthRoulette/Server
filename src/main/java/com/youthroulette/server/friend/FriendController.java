package com.youthroulette.server.friend;

import com.youthroulette.server.friend.dto.FriendListResponse;
import com.youthroulette.server.friend.dto.FriendRequest;
import com.youthroulette.server.friend.dto.FriendResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) { this.friendService = friendService; }

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendResponse request(@Valid @RequestBody FriendRequest request) { return friendService.request(request); }

    @GetMapping("/requests/received")
    public List<FriendResponse> receivedRequests() { return friendService.receivedRequests(); }

    @PatchMapping("/{friendId}/accept")
    public FriendResponse accept(@PathVariable Long friendId) { return friendService.accept(friendId); }

    @PatchMapping("/{friendId}/reject")
    public FriendResponse reject(@PathVariable Long friendId) { return friendService.reject(friendId); }

    @GetMapping
    public List<FriendListResponse> friends() { return friendService.friends(); }

    @DeleteMapping("/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long friendId) { friendService.delete(friendId); }
}
