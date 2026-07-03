package com.youthroulette.server.image;

import com.youthroulette.server.image.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
public class ImageService {
    private final S3Presigner s3Presigner;
    private final String bucket;

    public ImageService(
            S3Presigner s3Presigner,
            @Value("${aws.bucket}") String bucket
    ) {
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;
    }

    public PresignedUrlResponse createPresignedUrl(
            String fileName,
            String contentType) {
        String objectKey = "images/" + UUID.randomUUID() + "-" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        URL uploadUrl = s3Presigner.presignPutObject(presignRequest).url();

        String imageUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + objectKey;

        return new PresignedUrlResponse(uploadUrl.toString(), imageUrl);
    }

}
