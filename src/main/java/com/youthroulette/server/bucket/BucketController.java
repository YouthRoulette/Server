package com.youthroulette.server.bucket;

import com.youthroulette.server.bucket.dto.BucketRequest;
import com.youthroulette.server.bucket.dto.BucketResponse;
import com.youthroulette.server.bucket.dto.MessageResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buckets")
public class BucketController {
    private final BucketService bucketService;

    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BucketResponse create(@Valid @RequestBody BucketRequest request) {
        return bucketService.create(request);
    }

    @GetMapping
    public List<BucketResponse> myBuckets(
            @RequestParam(required = false) BucketStatus status,
            @RequestParam(required = false) Boolean verified
    ) {
        return bucketService.myBuckets(status, verified);
    }

    @DeleteMapping("/{bucketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public MessageResponse delete(@PathVariable Long bucketId) {
        bucketService.delete(bucketId);
        return new MessageResponse("버킷이 삭제되었습니다.");
    }

    @PostMapping("/roulette")
    public BucketResponse roulette() {
        return bucketService.roulette();
    }

    @PatchMapping("/{bucketId}/start")
    public BucketResponse start(@PathVariable Long bucketId) {
        return bucketService.start(bucketId);
    }

    @PatchMapping("/{bucketId}/complete")
    public BucketResponse complete(@PathVariable Long bucketId) {
        return bucketService.complete(bucketId);
    }

    @PatchMapping("/{bucketId}/incomplete")
    public BucketResponse incomplete(@PathVariable Long bucketId) {
        return bucketService.incomplete(bucketId);
    }
}
