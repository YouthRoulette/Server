package com.youthroulette.server.bucket;

import com.youthroulette.server.bucket.dto.BucketRequest;
import com.youthroulette.server.bucket.dto.BucketResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buckets")
public class BucketController {
    private final BucketService bucketService;

    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BucketResponse create(@Valid @RequestBody BucketRequest request) { return bucketService.create(request); }

    @GetMapping
    public List<BucketResponse> myBuckets(
            @RequestParam(required = false) BucketStatus status
    ) {
        return bucketService.myBuckets(status);
    }
    @DeleteMapping("/{bucketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long bucketId) { bucketService.delete(bucketId); }

    @PostMapping("/roulette")
    public BucketResponse roulette() { return bucketService.roulette(); }

    @PatchMapping("/{bucketId}/start")
    public BucketResponse start(@PathVariable Long bucketId) { return bucketService.start(bucketId); }

    @PatchMapping("/{bucketId}/complete")
    public BucketResponse complete(@PathVariable Long bucketId) { return bucketService.complete(bucketId); }
}
