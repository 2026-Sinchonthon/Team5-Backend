package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "job_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPost {

    public static final int MAX_IMAGE_COUNT = 10;

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

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @BatchSize(size = 50)
    private List<JobPostImage> images = new ArrayList<>();

    private JobPost(
            Long ownerId,
            String title,
            String description,
            String rawRequest,
            JobPostCategory category,
            Integer budget,
            OffsetDateTime deadline,
            Integer revisionLimit
    ) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.rawRequest = rawRequest;
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
            JobPostCategory category,
            Integer budget,
            OffsetDateTime deadline,
            Integer revisionLimit,
            List<String> imageUrls
    ) {
        JobPost jobPost = new JobPost(
                ownerId,
                title,
                description,
                rawRequest,
                category,
                budget,
                deadline,
                revisionLimit == null ? 4 : revisionLimit
        );

        if (imageUrls != null) {
            imageUrls.forEach(jobPost::addImage);
        }

        return jobPost;
    }

    public boolean isOwnedBy(Long memberId) {
        return this.ownerId.equals(memberId);
    }

    public boolean isEditable() {
        return this.status == JobPostStatus.OPEN;
    }

    public void update(String title, String description, Integer budget, OffsetDateTime deadline) {
        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
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

    public boolean canAddImage() {
        return images.size() < MAX_IMAGE_COUNT;
    }

    public JobPostImage addImage(String imageUrl) {
        JobPostImage image = JobPostImage.of(this, imageUrl, images.size());
        images.add(image);
        return image;
    }

    public void removeImage(Long imageId) {
        images.removeIf(image -> image.getId().equals(imageId));
    }
}
