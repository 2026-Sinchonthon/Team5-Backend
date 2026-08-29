package com.sinchonthon.team5.odyssey.jobapplication.domain;

import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(
        name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_application",
                        columnNames = {"job_post_id", "student_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private JobApplicationStatus status;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private OffsetDateTime appliedAt;

    @Column(name = "decided_at")
    private OffsetDateTime decidedAt;

    private JobApplication(
            Long jobPostId,
            Long studentId,
            String message,
            String imageUrl
    ) {
        this.jobPostId = jobPostId;
        this.studentId = studentId;
        this.message = message;
        this.imageUrl = imageUrl;
        this.status = JobApplicationStatus.PENDING;
    }

    public static JobApplication create(
            Long jobPostId,
            Long studentId,
            String message,
            String imageUrl
    ) {
        return new JobApplication(jobPostId, studentId, message, imageUrl);
    }

    public boolean isPending() {
        return status == JobApplicationStatus.PENDING;
    }

    public boolean isAppliedBy(Long studentId) {
        return this.studentId.equals(studentId);
    }

    public void cancel() {
        this.status = JobApplicationStatus.CANCELED;
    }

    public void accept() {
        this.status = JobApplicationStatus.ACCEPTED;
        this.decidedAt = OffsetDateTime.now();
    }

    public void reject() {
        this.status = JobApplicationStatus.REJECTED;
        this.decidedAt = OffsetDateTime.now();
    }
}
