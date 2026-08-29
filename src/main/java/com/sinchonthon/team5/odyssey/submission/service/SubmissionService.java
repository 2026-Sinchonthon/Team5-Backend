package com.sinchonthon.team5.odyssey.submission.service;

import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.global.storage.FileStorageErrorCode;
import com.sinchonthon.team5.odyssey.global.storage.FileStorageService;
import com.sinchonthon.team5.odyssey.global.storage.StoredFile;
import com.sinchonthon.team5.odyssey.jobapplication.JobApplicationErrorCode;
import com.sinchonthon.team5.odyssey.jobapplication.domain.JobApplication;
import com.sinchonthon.team5.odyssey.jobapplication.repository.JobApplicationRepository;
import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.JobPostErrorCode;
import com.sinchonthon.team5.odyssey.jobpost.JobPostRepository;
import com.sinchonthon.team5.odyssey.matching.MatchingErrorCode;
import com.sinchonthon.team5.odyssey.matching.domain.Matching;
import com.sinchonthon.team5.odyssey.matching.repository.MatchingRepository;
import com.sinchonthon.team5.odyssey.submission.code.SubmissionErrorCode;
import com.sinchonthon.team5.odyssey.submission.domain.RevisionRequest;
import com.sinchonthon.team5.odyssey.submission.domain.Submission;
import com.sinchonthon.team5.odyssey.submission.domain.SubmissionFile;
import com.sinchonthon.team5.odyssey.submission.dto.RevisionRequestCreateRequest;
import com.sinchonthon.team5.odyssey.submission.dto.RevisionRequestCreateResponse;
import com.sinchonthon.team5.odyssey.submission.dto.SubmissionApproveResponse;
import com.sinchonthon.team5.odyssey.submission.dto.SubmissionCreateResponse;
import com.sinchonthon.team5.odyssey.submission.dto.SubmissionHistoryResponse;
import com.sinchonthon.team5.odyssey.submission.repository.RevisionRequestRepository;
import com.sinchonthon.team5.odyssey.submission.repository.SubmissionFileRepository;
import com.sinchonthon.team5.odyssey.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private static final String STORAGE_DIRECTORY = "submissions";

    private final SubmissionRepository submissionRepository;
    private final SubmissionFileRepository submissionFileRepository;
    private final RevisionRequestRepository revisionRequestRepository;
    private final MatchingRepository matchingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;
    private final ObjectProvider<FileStorageService> fileStorageServiceProvider;

    @Transactional
    public SubmissionCreateResponse submit(
            Long studentId,
            Long matchingId,
            String description,
            List<MultipartFile> files
    ) {
        validateFiles(files);

        Matching matching = findMatchingForUpdateOrThrow(matchingId);
        JobApplication application = findApplicationOrThrow(matching.getApplicationId());
        validateStudent(application, studentId);

        if (!matching.canSubmit()) {
            throw new GeneralException(SubmissionErrorCode.INVALID_STATUS);
        }
        validateDeadline(matching);

        int nextRound = submissionRepository
                .findTopByMatchingIdOrderByRoundNumberDesc(matchingId)
                .map(previous -> previous.getRoundNumber() + 1)
                .orElse(1);

        FileStorageService storageService = getStorageService();
        List<StoredFile> uploadedFiles = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                uploadedFiles.add(storageService.upload(file, STORAGE_DIRECTORY));
            }

            Submission submission = submissionRepository.saveAndFlush(
                    Submission.create(matchingId, nextRound, description)
            );

            List<SubmissionFile> submissionFiles = uploadedFiles.stream()
                    .map(file -> SubmissionFile.create(
                            submission.getId(),
                            file.originalName(),
                            file.fileUrl(),
                            file.contentType(),
                            file.fileSize()
                    ))
                    .toList();

            List<SubmissionFile> savedFiles =
                    submissionFileRepository.saveAllAndFlush(submissionFiles);

            matching.submit();
            return SubmissionCreateResponse.of(submission, savedFiles);
        } catch (RuntimeException exception) {
            deleteUploadedFilesQuietly(storageService, uploadedFiles);
            throw exception;
        }
    }

    public List<SubmissionHistoryResponse> getHistory(
            Long memberId,
            Long matchingId
    ) {
        Matching matching = findMatchingOrThrow(matchingId);
        JobApplication application = findApplicationOrThrow(matching.getApplicationId());
        JobPost jobPost = findJobPostOrThrow(matching.getJobPostId());
        validateParticipant(memberId, application, jobPost);

        List<Submission> submissions =
                submissionRepository.findAllByMatchingIdOrderByRoundNumberAsc(matchingId);

        if (submissions.isEmpty()) {
            return List.of();
        }

        List<Long> submissionIds = submissions.stream().map(Submission::getId).toList();
        Map<Long, List<SubmissionFile>> filesBySubmissionId = submissionFileRepository
                .findAllBySubmissionIdInOrderByIdAsc(submissionIds)
                .stream()
                .collect(Collectors.groupingBy(SubmissionFile::getSubmissionId));
        Map<Long, RevisionRequest> revisionBySubmissionId = revisionRequestRepository
                .findAllBySubmissionIdIn(submissionIds)
                .stream()
                .collect(Collectors.toMap(RevisionRequest::getSubmissionId, Function.identity()));

        return submissions.stream()
                .map(submission -> SubmissionHistoryResponse.of(
                        submission,
                        filesBySubmissionId.getOrDefault(
                                submission.getId(),
                                Collections.emptyList()
                        ),
                        revisionBySubmissionId.get(submission.getId())
                ))
                .toList();
    }

    @Transactional
    public RevisionRequestCreateResponse requestRevision(
            Long ownerId,
            Long submissionId,
            RevisionRequestCreateRequest request
    ) {
        Submission submission = findSubmissionForUpdateOrThrow(submissionId);
        Matching matching = findMatchingForUpdateOrThrow(submission.getMatchingId());
        JobPost jobPost = findJobPostForUpdateOrThrow(matching.getJobPostId());

        validateOwner(jobPost, ownerId);
        validateSubmitted(submission, matching);

        if (!matching.canRequestRevision(jobPost.getRevisionLimit())) {
            throw new GeneralException(SubmissionErrorCode.REVISION_LIMIT_EXCEEDED);
        }
        if (revisionRequestRepository.existsBySubmissionId(submissionId)) {
            throw new GeneralException(SubmissionErrorCode.REVISION_ALREADY_EXISTS);
        }

        submission.requestRevision();
        matching.requestRevision();

        RevisionRequest revisionRequest = revisionRequestRepository.save(
                RevisionRequest.create(submissionId, request.reason())
        );

        return RevisionRequestCreateResponse.of(
                revisionRequest,
                matching.getRevisionCount(),
                jobPost.getRevisionLimit()
        );
    }

    @Transactional
    public SubmissionApproveResponse approve(
            Long ownerId,
            Long submissionId
    ) {
        Submission submission = findSubmissionForUpdateOrThrow(submissionId);
        Matching matching = findMatchingForUpdateOrThrow(submission.getMatchingId());
        JobPost jobPost = findJobPostForUpdateOrThrow(matching.getJobPostId());

        validateOwner(jobPost, ownerId);
        validateSubmitted(submission, matching);

        submission.approve();
        matching.complete();
        jobPost.complete();

        return SubmissionApproveResponse.of(submission, matching);
    }

    private Matching findMatchingOrThrow(Long matchingId) {
        return matchingRepository.findById(matchingId)
                .orElseThrow(() -> new GeneralException(MatchingErrorCode.NOT_FOUND));
    }

    private Matching findMatchingForUpdateOrThrow(Long matchingId) {
        return matchingRepository.findByIdForUpdate(matchingId)
                .orElseThrow(() -> new GeneralException(MatchingErrorCode.NOT_FOUND));
    }

    private JobApplication findApplicationOrThrow(Long applicationId) {
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new GeneralException(JobApplicationErrorCode.NOT_FOUND));
    }

    private JobPost findJobPostOrThrow(Long jobPostId) {
        return jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new GeneralException(JobPostErrorCode.NOT_FOUND));
    }

    private JobPost findJobPostForUpdateOrThrow(Long jobPostId) {
        return jobPostRepository.findByIdForUpdate(jobPostId)
                .orElseThrow(() -> new GeneralException(JobPostErrorCode.NOT_FOUND));
    }

    private Submission findSubmissionForUpdateOrThrow(Long submissionId) {
        return submissionRepository.findByIdForUpdate(submissionId)
                .orElseThrow(() -> new GeneralException(SubmissionErrorCode.NOT_FOUND));
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.stream().anyMatch(MultipartFile::isEmpty)) {
            throw new GeneralException(SubmissionErrorCode.FILE_REQUIRED);
        }
    }

    private void validateStudent(JobApplication application, Long studentId) {
        if (!application.isAppliedBy(studentId)) {
            throw new GeneralException(SubmissionErrorCode.FORBIDDEN);
        }
    }

    private void validateDeadline(Matching matching) {
        if (OffsetDateTime.now().isAfter(matching.getDeadline())) {
            throw new GeneralException(SubmissionErrorCode.DEADLINE_EXCEEDED);
        }
    }

    private void validateOwner(JobPost jobPost, Long ownerId) {
        if (!jobPost.isOwnedBy(ownerId)) {
            throw new GeneralException(SubmissionErrorCode.FORBIDDEN);
        }
    }

    private void validateParticipant(
            Long memberId,
            JobApplication application,
            JobPost jobPost
    ) {
        if (!application.isAppliedBy(memberId) && !jobPost.isOwnedBy(memberId)) {
            throw new GeneralException(SubmissionErrorCode.FORBIDDEN);
        }
    }

    private void validateSubmitted(Submission submission, Matching matching) {
        if (!submission.isSubmitted() || !matching.isSubmitted()) {
            throw new GeneralException(SubmissionErrorCode.INVALID_STATUS);
        }
    }

    private FileStorageService getStorageService() {
        FileStorageService storageService = fileStorageServiceProvider.getIfAvailable();
        if (storageService == null) {
            throw new GeneralException(FileStorageErrorCode.UPLOAD_FAILED);
        }
        return storageService;
    }

    private void deleteUploadedFilesQuietly(
            FileStorageService storageService,
            List<StoredFile> uploadedFiles
    ) {
        for (StoredFile file : uploadedFiles) {
            try {
                storageService.delete(file.fileUrl());
            } catch (RuntimeException ignored) {
                // 원래 예외를 유지하고 정리 실패가 덮어쓰지 않도록 한다.
            }
        }
    }
}
