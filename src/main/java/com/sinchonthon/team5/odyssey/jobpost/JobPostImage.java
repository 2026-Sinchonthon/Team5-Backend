package com.sinchonthon.team5.odyssey.jobpost;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "job_post_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    private JobPostImage(JobPost jobPost, String imageUrl, Integer sortOrder) {
        this.jobPost = jobPost;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public static JobPostImage of(JobPost jobPost, String imageUrl, Integer sortOrder) {
        return new JobPostImage(jobPost, imageUrl, sortOrder);
    }
}
