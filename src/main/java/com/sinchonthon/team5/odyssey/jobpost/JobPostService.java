package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.global.api.PageResponse;
import com.sinchonthon.team5.odyssey.global.code.GeneralErrorCode;
import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.global.storage.FileStorageErrorCode;
import com.sinchonthon.team5.odyssey.global.storage.FileStorageService;
import com.sinchonthon.team5.odyssey.global.storage.StoredFile;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostCreateRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostCreateResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostDetailResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostImageResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostListItemResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostSearchCondition;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostUpdateRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostUpdateResponse;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {

    private static final String STORAGE_DIRECTORY = "job-posts";

    private final JobPostRepository jobPostRepository;
    private final ObjectProvider<FileStorageService> fileStorageServiceProvider;

    @Transactional
    public JobPostCreateResponse create(
            Long ownerId,
            JobPostCreateRequest request,
            List<MultipartFile> images
    ) {
        List<MultipartFile> uploadTargets =
                images == null ? List.of() : images;

        validateImages(uploadTargets);

        FileStorageService storageService =
                uploadTargets.isEmpty() ? null : getStorageService();

        List<StoredFile> uploadedFiles = new ArrayList<>();

        try {
            if (storageService != null) {
                for (MultipartFile image : uploadTargets) {
                    uploadedFiles.add(
                            storageService.upload(image, STORAGE_DIRECTORY)
                    );
                }
            }

            List<String> imageUrls = uploadedFiles.stream()
                    .map(StoredFile::fileUrl)
                    .toList();

            JobPost jobPost = JobPost.create(
                    ownerId,
                    request.title(),
                    request.description(),
                    request.rawRequest(),
                    request.category(),
                    request.budget(),
                    request.deadline(),
                    request.revisionLimit(),
                    imageUrls
            );

            JobPost savedJobPost =
                    jobPostRepository.saveAndFlush(jobPost);

            return JobPostCreateResponse.from(savedJobPost);
        } catch (RuntimeException exception) {
            deleteUploadedFilesQuietly(storageService, uploadedFiles);
            throw exception;
        }
    }

    public PageResponse<JobPostListItemResponse> getList(
            JobPostSearchCondition condition,
            int page,
            int size
    ) {
        Specification<JobPost> spec = Specification
                .where(JobPostSpecs.categoryEquals(condition.category()))
                .and(JobPostSpecs.statusEquals(condition.status()))
                .and(JobPostSpecs.budgetGreaterThanOrEqual(
                        condition.minBudget()
                ))
                .and(JobPostSpecs.budgetLessThanOrEqual(
                        condition.maxBudget()
                ));

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                resolveSort(condition.sort())
        );

        Page<JobPost> result =
                jobPostRepository.findAll(spec, pageRequest);

        return PageResponse.from(
                result,
                JobPostListItemResponse::from
        );
    }

    public JobPostDetailResponse getDetail(Long jobPostId) {
        return JobPostDetailResponse.from(
                findJobPostOrThrow(jobPostId)
        );
    }

    public List<JobPostListItemResponse> getMyList(Long ownerId) {
        return jobPostRepository
                .findAllByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(JobPostListItemResponse::from)
                .toList();
    }

    @Transactional
    public JobPostUpdateResponse update(
            Long ownerId,
            Long jobPostId,
            JobPostUpdateRequest request
    ) {
        validateNotBlankIfPresent(request.title());
        validateNotBlankIfPresent(request.description());

        JobPost jobPost = findJobPostOrThrow(jobPostId);

        validateOwner(jobPost, ownerId);
        validateEditable(jobPost);

        jobPost.update(
                request.title(),
                request.description(),
                request.budget(),
                request.deadline()
        );

        return JobPostUpdateResponse.from(jobPost);
    }

    @Transactional
    public void cancel(Long ownerId, Long jobPostId) {
        JobPost jobPost = findJobPostOrThrow(jobPostId);

        validateOwner(jobPost, ownerId);
        validateEditable(jobPost);

        jobPost.cancel();
    }

    @Transactional
    public JobPostImageResponse addImage(
            Long ownerId,
            Long jobPostId,
            MultipartFile image
    ) {
        validateImage(image);

        JobPost jobPost = jobPostRepository
                .findByIdForUpdate(jobPostId)
                .orElseThrow(() ->
                        new GeneralException(JobPostErrorCode.NOT_FOUND)
                );

        validateOwner(jobPost, ownerId);
        validateEditable(jobPost);

        if (!jobPost.canAddImage()) {
            throw new GeneralException(
                    JobPostErrorCode.IMAGE_LIMIT_EXCEEDED
            );
        }

        FileStorageService storageService = getStorageService();
        StoredFile uploadedFile =
                storageService.upload(image, STORAGE_DIRECTORY);

        try {
            JobPostImage jobPostImage =
                    jobPost.addImage(uploadedFile.fileUrl());

            jobPostRepository.flush();

            return JobPostImageResponse.from(jobPostImage);
        } catch (RuntimeException exception) {
            deleteUploadedFileQuietly(
                    storageService,
                    uploadedFile.fileUrl()
            );
            throw exception;
        }
    }

    @Transactional
    public void removeImage(
            Long ownerId,
            Long jobPostId,
            Long imageId
    ) {
        JobPost jobPost = findJobPostOrThrow(jobPostId);

        validateOwner(jobPost, ownerId);
        validateEditable(jobPost);

        JobPostImage image = jobPost.getImages()
                .stream()
                .filter(jobPostImage ->
                        jobPostImage.getId().equals(imageId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new GeneralException(
                                JobPostErrorCode.IMAGE_NOT_FOUND
                        )
                );

        String imageUrl = image.getImageUrl();

        jobPost.removeImage(imageId);
        jobPostRepository.flush();

        FileStorageService storageService = getStorageService();

        try {
            storageService.delete(imageUrl);
        } catch (RuntimeException exception) {
            throw new GeneralException(
                    FileStorageErrorCode.DELETE_FAILED
            );
        }
    }

    private JobPost findJobPostOrThrow(Long jobPostId) {
        return jobPostRepository.findById(jobPostId)
                .orElseThrow(() ->
                        new GeneralException(
                                JobPostErrorCode.NOT_FOUND
                        )
                );
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

    private void validateEditable(JobPost jobPost) {
        if (!jobPost.isEditable()) {
            throw new GeneralException(
                    JobPostErrorCode.NOT_EDITABLE
            );
        }
    }

    private void validateNotBlankIfPresent(String value) {
        if (value != null && value.isBlank()) {
            throw new GeneralException(
                    GeneralErrorCode.BAD_REQUEST
            );
        }
    }

    private void validateImages(List<MultipartFile> images) {
        if (images.size() > JobPost.MAX_IMAGE_COUNT) {
            throw new GeneralException(
                    JobPostErrorCode.IMAGE_LIMIT_EXCEEDED
            );
        }

        for (MultipartFile image : images) {
            validateImage(image);
        }
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new GeneralException(
                    GeneralErrorCode.BAD_REQUEST
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

    private void deleteUploadedFilesQuietly(
            FileStorageService storageService,
            List<StoredFile> uploadedFiles
    ) {
        if (storageService == null) {
            return;
        }

        for (StoredFile uploadedFile : uploadedFiles) {
            deleteUploadedFileQuietly(
                    storageService,
                    uploadedFile.fileUrl()
            );
        }
    }

    private void deleteUploadedFileQuietly(
            FileStorageService storageService,
            String fileUrl
    ) {
        try {
            storageService.delete(fileUrl);
        } catch (RuntimeException ignored) {
            // 기존 예외가 파일 삭제 예외로 덮어쓰이지 않도록 한다.
        }
    }

    private Sort resolveSort(JobPostSortType sortType) {
        return switch (
                sortType == null
                        ? JobPostSortType.LATEST
                        : sortType
                ) {
            case DEADLINE ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "deadline"
                    );

            case BUDGET_HIGH ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "budget"
                    );

            case LATEST ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                    );
        };
    }
}
