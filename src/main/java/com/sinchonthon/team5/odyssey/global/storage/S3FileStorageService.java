package com.sinchonthon.team5.odyssey.global.storage;

import com.sinchonthon.team5.odyssey.global.exception.GeneralException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;
    private final String bucket;
    private final String region;

    public S3FileStorageService(
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.s3.region}") String region
    ) {
        this.bucket = bucket;
        this.region = region;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    @Override
    public StoredFile upload(MultipartFile file, String directory) {
        String key = buildKey(directory, file.getOriginalFilename());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException exception) {
            log.error("S3 파일 업로드 실패: key={}", key, exception);
            throw new GeneralException(FileStorageErrorCode.UPLOAD_FAILED);
        }

        return new StoredFile(
                file.getOriginalFilename(),
                buildUrl(key),
                file.getContentType(),
                file.getSize()
        );
    }

    @Override
    public void delete(String fileUrl) {
        String key = extractKey(fileUrl);

        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
        } catch (RuntimeException exception) {
            log.error("S3 파일 삭제 실패: key={}", key, exception);
            throw new GeneralException(FileStorageErrorCode.DELETE_FAILED);
        }
    }

    private String buildKey(String directory, String originalFilename) {
        String extension = extractExtension(originalFilename);
        String fileName = UUID.randomUUID() + extension;

        return directory == null || directory.isBlank()
                ? fileName
                : directory + "/" + fileName;
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }

        int lastDot = originalFilename.lastIndexOf('.');
        return lastDot == -1 ? "" : originalFilename.substring(lastDot);
    }

    private String buildUrl(String key) {
        return "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, key);
    }

    private String extractKey(String fileUrl) {
        String path = URI.create(fileUrl).getPath();
        String decoded = URLDecoder.decode(path, StandardCharsets.UTF_8);

        return decoded.startsWith("/") ? decoded.substring(1) : decoded;
    }
}
