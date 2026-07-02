package com.youthroulette.server.image;

import com.youthroulette.server.image.dto.PresignedUrlRequest;
import com.youthroulette.server.image.dto.PresignedUrlResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @PostMapping("/presigned-url")
    public PresignedUrlResponse presignedUrl(@Valid @RequestBody PresignedUrlRequest request) {
        String objectKey = UUID.randomUUID() + "-" + request.fileName();
        String imageUrl = "https://example.com/images/" + objectKey;
        return new PresignedUrlResponse(imageUrl + "?uploadToken=hackathon", imageUrl);
    }
}
