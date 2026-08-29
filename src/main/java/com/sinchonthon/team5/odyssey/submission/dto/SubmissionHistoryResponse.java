package com.sinchonthon.team5.odyssey.submission.dto;

import com.sinchonthon.team5.odyssey.submission.domain.RevisionRequest;
import com.sinchonthon.team5.odyssey.submission.domain.Submission;
import com.sinchonthon.team5.odyssey.submission.domain.SubmissionFile;
import com.sinchonthon.team5.odyssey.submission.enums.SubmissionStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record SubmissionHistoryResponse(
        Long submissionId,
        Integer roundNumber,
        String description,
        SubmissionStatus status,
        OffsetDateTime submittedAt,
        List<FileResponse> files,
        RevisionResponse revisionRequest
) {
    public static SubmissionHistoryResponse of(
            Submission submission,
            List<SubmissionFile> files,
            RevisionRequest revisionRequest
    ) {
        return new SubmissionHistoryResponse(
                submission.getId(),
                submission.getRoundNumber(),
                submission.getDescription(),
                submission.getStatus(),
                submission.getSubmittedAt(),
                files.stream().map(FileResponse::from).toList(),
                revisionRequest == null ? null : RevisionResponse.from(revisionRequest)
        );
    }

    public record FileResponse(
            Long fileId,
            String originalName,
            String fileUrl,
            String contentType,
            Long fileSize
    ) {
        public static FileResponse from(SubmissionFile file) {
            return new FileResponse(
                    file.getId(),
                    file.getOriginalName(),
                    file.getFileUrl(),
                    file.getContentType(),
                    file.getFileSize()
            );
        }
    }

    public record RevisionResponse(
            Long revisionRequestId,
            String reason,
            OffsetDateTime requestedAt
    ) {
        public static RevisionResponse from(RevisionRequest request) {
            return new RevisionResponse(
                    request.getId(),
                    request.getReason(),
                    request.getRequestedAt()
            );
        }
    }
}
