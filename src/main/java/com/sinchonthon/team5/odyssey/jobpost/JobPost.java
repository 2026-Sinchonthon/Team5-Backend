package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "job_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "raw_request", columnDefinition = "TEXT")
    private String rawRequest;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobPostCategory category;

    @Column(nullable = false)
    private Integer budget;

    @Column(nullable = false)
    private OffsetDateTime deadline;

    @Column(name = "revision_limit", nullable = false)
    private Integer revisionLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobPostStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    private JobPost(
            Long ownerId,
            String title,
            String description,
            String rawRequest,
            String imageUrl,
            JobPostCategory category,
            Integer budget,
            OffsetDateTime deadline,
            Integer revisionLimit
    ) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.rawRequest = rawRequest;
        this.imageUrl = imageUrl;
        this.category = category;
        this.budget = budget;
        this.deadline = deadline;
        this.revisionLimit = revisionLimit;
        this.status = JobPostStatus.OPEN;
    }

    public static JobPost create(
            Long ownerId,
            String title,
            String description,
            String rawRequest,
            String imageUrl,
            JobPostCategory category,
            Integer budget,
            OffsetDateTime deadline,
            Integer revisionLimit
    ) {
        return new JobPost(
                ownerId,
                title,
                description,
                rawRequest,
                imageUrl,
                category,
                budget,
                deadline,
                revisionLimit == null ? 4 : revisionLimit
        );
    }

    public boolean isOwnedBy(Long memberId) {
        return this.ownerId.equals(memberId);
    }

    public boolean isEditable() {
        return this.status == JobPostStatus.OPEN;
    }

    public void update(
            String title,
            String description,
            String imageUrl,
            Integer budget,
            OffsetDateTime deadline
    ) {
        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
        if (budget != null) {
            this.budget = budget;
        }
        if (deadline != null) {
            this.deadline = deadline;
        }
    }

    public void cancel() {
        this.status = JobPostStatus.CANCELED;
    }
}
