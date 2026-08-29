package com.sinchonthon.team5.odyssey.submission.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "submission_files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    private SubmissionFile(
            Long submissionId,
            String originalName,
            String fileUrl,
            String contentType,
            Long fileSize
    ) {
        this.submissionId = submissionId;
        this.originalName = originalName;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    public static SubmissionFile create(
            Long submissionId,
            String originalName,
            String fileUrl,
            String contentType,
            Long fileSize
    ) {
        return new SubmissionFile(
                submissionId,
                originalName,
                fileUrl,
                contentType,
                fileSize
        );
    }
}
