package com.sinchonthon.team5.odyssey.submission.domain;

import com.sinchonthon.team5.odyssey.submission.enums.SubmissionStatus;
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
        name = "submissions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_submission_round",
                columnNames = {"matching_id", "round_number"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "matching_id", nullable = false)
    private Long matchingId;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubmissionStatus status;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private OffsetDateTime submittedAt;

    private Submission(
            Long matchingId,
            Integer roundNumber,
            String description
    ) {
        this.matchingId = matchingId;
        this.roundNumber = roundNumber;
        this.description = description;
        this.status = SubmissionStatus.SUBMITTED;
    }

    public static Submission create(
            Long matchingId,
            Integer roundNumber,
            String description
    ) {
        return new Submission(matchingId, roundNumber, description);
    }

    public boolean isSubmitted() {
        return status == SubmissionStatus.SUBMITTED;
    }

    public void requestRevision() {
        this.status = SubmissionStatus.REVISION_REQUESTED;
    }

    public void approve() {
        this.status = SubmissionStatus.APPROVED;
    }
}
