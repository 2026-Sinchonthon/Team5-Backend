package com.sinchonthon.team5.odyssey.matching.domain;

import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(
        name = "matchings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_matching_job_post", columnNames = "job_post_id"),
                @UniqueConstraint(name = "uk_matching_application", columnNames = "application_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "agreed_amount", nullable = false)
    private Integer agreedAmount;

    @Column(nullable = false)
    private OffsetDateTime deadline;

    @Column(name = "revision_count", nullable = false)
    private Integer revisionCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchingStatus status;

    @CreationTimestamp
    @Column(name = "matched_at", nullable = false, updatable = false)
    private OffsetDateTime matchedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    private Matching(
            Long jobPostId,
            Long applicationId,
            Integer agreedAmount,
            OffsetDateTime deadline
    ) {
        this.jobPostId = jobPostId;
        this.applicationId = applicationId;
        this.agreedAmount = agreedAmount;
        this.deadline = deadline;
        this.revisionCount = 0;
        this.status = MatchingStatus.IN_PROGRESS;
    }

    public static Matching create(
            Long jobPostId,
            Long applicationId,
            Integer agreedAmount,
            OffsetDateTime deadline
    ) {
        return new Matching(jobPostId, applicationId, agreedAmount, deadline);
    }

    public boolean canSubmit() {
        return status == MatchingStatus.IN_PROGRESS
                || status == MatchingStatus.REVISION_REQUESTED;
    }

    public boolean isSubmitted() {
        return status == MatchingStatus.SUBMITTED;
    }

    public boolean canRequestRevision(Integer revisionLimit) {
        return revisionCount < revisionLimit;
    }

    public void submit() {
        this.status = MatchingStatus.SUBMITTED;
    }

    public void requestRevision() {
        this.revisionCount++;
        this.status = MatchingStatus.REVISION_REQUESTED;
    }

    public void complete() {
        this.status = MatchingStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }
}
