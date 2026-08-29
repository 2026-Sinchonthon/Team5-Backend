package com.sinchonthon.team5.odyssey.submission.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "revision_requests",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_revision_request_submission",
                columnNames = "submission_id"
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RevisionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Column(nullable = false, length = 1000)
    private String reason;

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private OffsetDateTime requestedAt;

    private RevisionRequest(Long submissionId, String reason) {
        this.submissionId = submissionId;
        this.reason = reason;
    }

    public static RevisionRequest create(Long submissionId, String reason) {
        return new RevisionRequest(submissionId, reason);
    }
}
