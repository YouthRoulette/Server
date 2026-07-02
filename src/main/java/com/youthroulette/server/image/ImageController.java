package com.youthroulette.server.image;

import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.common.ErrorCode;
import com.youthroulette.server.image.dto.PresignedUrlRequest;
import com.youthroulette.server.image.dto.PresignedUrlResponse;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    @PostMapping("/presigned-url")
    public PresignedUrlResponse presignedUrl(@Valid @RequestBody PresignedUrlRequest request) {
        if (!isSupportedImage(request.fileName())) {
            throw new ApiException(ErrorCode.INVALID_IMAGE_TYPE);
        }
        String objectKey = UUID.randomUUID() + "-" + request.fileName();
        String imageUrl = "https://example.com/images/" + objectKey;
        return new PresignedUrlResponse(imageUrl + "?uploadToken=hackathon", imageUrl);
    }

    private boolean isSupportedImage(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return false;
        }
        String extension = fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        return SUPPORTED_EXTENSIONS.contains(extension);
    }
}
