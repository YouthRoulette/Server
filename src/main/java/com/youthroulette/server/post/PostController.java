package com.youthroulette.server.post;

import com.youthroulette.server.post.dto.CreatePostRequest;
import com.youthroulette.server.post.dto.LikeResponse;
import com.youthroulette.server.post.dto.MessageResponse;
import com.youthroulette.server.post.dto.PostResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) { this.postService = postService; }

    @PostMapping("/{bucketId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@PathVariable Long bucketId, @Valid @RequestBody CreatePostRequest request) {
        return postService.create(bucketId, request);
    }

    @GetMapping("/feed")
    public List<PostResponse> feed() { return postService.feed(); }

    @GetMapping("/me")
    public List<PostResponse> myPosts() { return postService.myPosts(); }

    @DeleteMapping("/{postId}")
    public MessageResponse delete(@PathVariable Long postId) {
        postService.delete(postId);
        return new MessageResponse(postId, "인증글이 삭제되었습니다.");
    }

    @PostMapping("/{postId}/likes")
    public LikeResponse like(@PathVariable Long postId) { return postService.like(postId); }

    @DeleteMapping("/{postId}/likes")
    public LikeResponse unlike(@PathVariable Long postId) { return postService.unlike(postId); }
}
