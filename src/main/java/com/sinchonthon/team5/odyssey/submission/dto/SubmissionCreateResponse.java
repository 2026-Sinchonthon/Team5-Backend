package com.sinchonthon.team5.odyssey.submission.dto;

import com.sinchonthon.team5.odyssey.submission.domain.Submission;
import com.sinchonthon.team5.odyssey.submission.domain.SubmissionFile;
import com.sinchonthon.team5.odyssey.submission.enums.SubmissionStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record SubmissionCreateResponse(
        Long submissionId,
        Long matchingId,
        Integer roundNumber,
        String description,
        SubmissionStatus status,
        List<FileResponse> files,
        OffsetDateTime submittedAt
) {
    public static SubmissionCreateResponse of(
            Submission submission,
            List<SubmissionFile> files
    ) {
        return new SubmissionCreateResponse(
                submission.getId(),
                submission.getMatchingId(),
                submission.getRoundNumber(),
                submission.getDescription(),
                submission.getStatus(),
                files.stream().map(FileResponse::from).toList(),
                submission.getSubmittedAt()
        );
    }

    public record FileResponse(
            Long fileId,
            String originalName,
            String fileUrl
    ) {
        public static FileResponse from(SubmissionFile file) {
            return new FileResponse(
                    file.getId(),
                    file.getOriginalName(),
                    file.getFileUrl()
            );
        }
    }
}
