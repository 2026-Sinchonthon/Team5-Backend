package com.sinchonthon.team5.odyssey.jobapplication.service;

import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.global.storage.FileStorageErrorCode;
import com.sinchonthon.team5.odyssey.global.storage.FileStorageService;
import com.sinchonthon.team5.odyssey.global.storage.StoredFile;
import com.sinchonthon.team5.odyssey.jobapplication.JobApplicationErrorCode;
import com.sinchonthon.team5.odyssey.jobapplication.domain.JobApplication;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicantResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCancelResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCreateResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationSummaryResponse;
import com.sinchonthon.team5.odyssey.jobapplication.repository.ApplicantProjection;
import com.sinchonthon.team5.odyssey.jobapplication.repository.ApplicationSummaryProjection;
import com.sinchonthon.team5.odyssey.jobapplication.repository.JobApplicationRepository;
import com.sinchonthon.team5.odyssey.jobpost.JobPost;
import com.sinchonthon.team5.odyssey.jobpost.JobPostErrorCode;
import com.sinchonthon.team5.odyssey.jobpost.JobPostRepository;
import com.sinchonthon.team5.odyssey.member.domain.SupportedUniversity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationService {

    private static final String STORAGE_DIRECTORY =
            "job-applications";

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;
    private final ObjectProvider<FileStorageService>
            fileStorageServiceProvider;

    @Transactional
    public ApplicationCreateResponse apply(
            Long studentId,
            Long jobPostId,
            String message,
            MultipartFile image
    ) {
        JobPost jobPost = findJobPostOrThrow(jobPostId);

        validateJobPostOpen(jobPost);
        validateNotDuplicated(jobPostId, studentId);

        StoredFile uploadedFile = null;
        FileStorageService storageService = null;

        try {
            if (image != null && !image.isEmpty()) {
                storageService = getStorageService();
                uploadedFile = storageService.upload(
                        image,
                        STORAGE_DIRECTORY
                );
            }

            String imageUrl = uploadedFile == null
                    ? null
                    : uploadedFile.fileUrl();

            JobApplication application =
                    JobApplication.create(
                            jobPostId,
                            studentId,
                            message,
                            imageUrl
                    );

            JobApplication savedApplication =
                    jobApplicationRepository.saveAndFlush(
                            application
                    );

            return new ApplicationCreateResponse(
                    savedApplication.getId(),
                    savedApplication.getJobPostId(),
                    savedApplication.getImageUrl(),
                    savedApplication.getStatus(),
                    savedApplication.getAppliedAt()
            );
        } catch (RuntimeException exception) {
            deleteUploadedFileQuietly(
                    storageService,
                    uploadedFile
            );
            throw exception;
        }
    }

    @Transactional
    public ApplicationCancelResponse cancel(
            Long studentId,
            Long applicationId
    ) {
        JobApplication application =
                findApplicationOrThrow(applicationId);

        validateStudent(application, studentId);
        validateCancelable(application);

        application.cancel();

        return new ApplicationCancelResponse(
                application.getId(),
                application.getStatus()
        );
    }

    public List<ApplicationSummaryResponse> getMyApplications(
            Long studentId
    ) {
        return jobApplicationRepository
                .findApplicationSummariesByStudentId(studentId)
                .stream()
                .map(this::toApplicationSummaryResponse)
                .toList();
    }

    public List<ApplicantResponse> getApplicants(
            Long ownerId,
            Long jobPostId
    ) {
        JobPost jobPost = findJobPostOrThrow(jobPostId);

        validateOwner(jobPost, ownerId);

        return jobApplicationRepository
                .findApplicantProjectionsByJobPostId(jobPostId)
                .stream()
                .map(this::toApplicantResponse)
                .toList();
    }

    private ApplicationSummaryResponse
    toApplicationSummaryResponse(
            ApplicationSummaryProjection projection
    ) {
        ApplicationSummaryResponse.JobPostSummary jobPost =
                new ApplicationSummaryResponse.JobPostSummary(
                        projection.getJobPostId(),
                        projection.getTitle(),
                        projection.getBusinessName(),
                        projection.getBudget(),
                        projection.getDeadline()
                );

        return new ApplicationSummaryResponse(
                projection.getApplicationId(),
                jobPost,
                projection.getStatus(),
                projection.getAppliedAt()
        );
    }

    private ApplicantResponse toApplicantResponse(
            ApplicantProjection projection
    ) {
        ApplicantResponse.StudentSummary student =
                new ApplicantResponse.StudentSummary(
                        projection.getMemberId(),
                        projection.getName(),
                        resolveUniversityName(
                                projection.getUniversityId()
                        ),
                        projection.getMajor(),
                        projection.getIntroduction()
                );

        return new ApplicantResponse(
                projection.getApplicationId(),
                student,
                projection.getMessage(),
                projection.getStatus(),
                projection.getAppliedAt()
        );
    }

    private JobPost findJobPostOrThrow(Long jobPostId) {
        return jobPostRepository.findById(jobPostId)
                .orElseThrow(() ->
                        new GeneralException(
                                JobPostErrorCode.NOT_FOUND
                        )
                );
    }

    private JobApplication findApplicationOrThrow(
            Long applicationId
    ) {
        return jobApplicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new GeneralException(
                                JobApplicationErrorCode.NOT_FOUND
                        )
                );
    }

    private void validateJobPostOpen(JobPost jobPost) {
        if (!jobPost.isEditable()) {
            throw new GeneralException(
                    JobApplicationErrorCode.JOB_POST_NOT_OPEN
            );
        }
    }

    private void validateNotDuplicated(
            Long jobPostId,
            Long studentId
    ) {
        boolean duplicated = jobApplicationRepository
                .existsByJobPostIdAndStudentId(
                        jobPostId,
                        studentId
                );

        if (duplicated) {
            throw new GeneralException(
                    JobApplicationErrorCode.DUPLICATE_APPLICATION
            );
        }
    }

    private void validateStudent(
            JobApplication application,
            Long studentId
    ) {
        if (!application.isAppliedBy(studentId)) {
            throw new GeneralException(
                    JobApplicationErrorCode.FORBIDDEN
            );
        }
    }

    private void validateCancelable(
            JobApplication application
    ) {
        if (!application.isPending()) {
            throw new GeneralException(
                    JobApplicationErrorCode.NOT_CANCELABLE
            );
        }
    }

    private void validateOwner(
            JobPost jobPost,
            Long ownerId
    ) {
        if (!jobPost.isOwnedBy(ownerId)) {
            throw new GeneralException(
                    JobPostErrorCode.FORBIDDEN
            );
        }
    }

    private FileStorageService getStorageService() {
        FileStorageService storageService =
                fileStorageServiceProvider.getIfAvailable();

        if (storageService == null) {
            throw new GeneralException(
                    FileStorageErrorCode.UPLOAD_FAILED
            );
        }

        return storageService;
    }

    private void deleteUploadedFileQuietly(
            FileStorageService storageService,
            StoredFile uploadedFile
    ) {
        if (storageService == null || uploadedFile == null) {
            return;
        }

        try {
            storageService.delete(
                    uploadedFile.fileUrl()
            );
        } catch (RuntimeException ignored) {
            // 기존 예외가 파일 삭제 예외로 덮어쓰이지 않도록 한다.
        }
    }

    private String resolveUniversityName(
            Long universityId
    ) {
        if (universityId == null) {
            return null;
        }

        return SupportedUniversity.fromId(universityId)
                .map(SupportedUniversity::getName)
                .orElse("알 수 없는 대학교");
    }
}
