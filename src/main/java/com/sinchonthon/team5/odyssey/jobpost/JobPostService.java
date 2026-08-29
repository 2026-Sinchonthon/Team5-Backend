package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.global.api.PageResponse;
import com.sinchonthon.team5.odyssey.global.code.GeneralErrorCode;
import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostCreateRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostCreateResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostDetailResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostImageAddRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostImageResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostListItemResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostSearchCondition;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostUpdateRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostUpdateResponse;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostSortType;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {

    private final JobPostRepository jobPostRepository;

    @Transactional
    public JobPostCreateResponse create(Long ownerId, JobPostCreateRequest request) {
        JobPost jobPost = JobPost.create(
                ownerId,
                request.title(),
                request.description(),
                request.rawRequest(),
                request.category(),
                request.budget(),
                request.deadline(),
                request.revisionLimit(),
                request.imageUrls()
        );

        return JobPostCreateResponse.from(jobPostRepository.save(jobPost));
    }

    public PageResponse<JobPostListItemResponse> getList(JobPostSearchCondition condition, int page, int size) {
        Specification<JobPost> spec = Specification
                .where(JobPostSpecs.categoryEquals(condition.category()))
                .and(JobPostSpecs.statusEquals(condition.status()))
                .and(JobPostSpecs.budgetGreaterThanOrEqual(condition.minBudget()))
                .and(JobPostSpecs.budgetLessThanOrEqual(condition.maxBudget()));

        PageRequest pageRequest = PageRequest.of(page, size, resolveSort(condition.sort()));
        Page<JobPost> result = jobPostRepository.findAll(spec, pageRequest);

        return PageResponse.from(result, JobPostListItemResponse::from);
    }

    public JobPostDetailResponse getDetail(Long jobPostId) {
        return JobPostDetailResponse.from(findJobPostOrThrow(jobPostId));
    }

    public List<JobPostListItemResponse> getMyList(Long ownerId) {
        return jobPostRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(JobPostListItemResponse::from)
                .toList();
    }

    @Transactional
    public JobPostUpdateResponse update(Long ownerId, Long jobPostId, JobPostUpdateRequest request) {
        validateNotBlankIfPresent(request.title());
        validateNotBlankIfPresent(request.description());

        JobPost jobPost = findJobPostOrThrow(jobPostId);
        validateOwner(jobPost, ownerId);
        validateEditable(jobPost);

        jobPost.update(request.title(), request.description(), request.budget(), request.deadline());

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
    public JobPostImageResponse addImage(Long ownerId, Long jobPostId, JobPostImageAddRequest request) {
        JobPost jobPost = jobPostRepository.findByIdForUpdate(jobPostId)
                .orElseThrow(() -> new GeneralException(JobPostErrorCode.NOT_FOUND));
        validateOwner(jobPost, ownerId);
        validateEditable(jobPost);

        return JobPostImageResponse.from(jobPost.addImage(request.imageUrl()));
    }

    @Transactional
    public void removeImage(Long ownerId, Long jobPostId, Long imageId) {
        JobPost jobPost = findJobPostOrThrow(jobPostId);
        validateOwner(jobPost, ownerId);
        validateEditable(jobPost);

        boolean exists = jobPost.getImages().stream().anyMatch(image -> image.getId().equals(imageId));
        if (!exists) {
            throw new GeneralException(JobPostErrorCode.IMAGE_NOT_FOUND);
        }

        jobPost.removeImage(imageId);
    }

    private JobPost findJobPostOrThrow(Long jobPostId) {
        return jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new GeneralException(JobPostErrorCode.NOT_FOUND));
    }

    private void validateOwner(JobPost jobPost, Long ownerId) {
        if (!jobPost.isOwnedBy(ownerId)) {
            throw new GeneralException(JobPostErrorCode.FORBIDDEN);
        }
    }

    private void validateEditable(JobPost jobPost) {
        if (!jobPost.isEditable()) {
            throw new GeneralException(JobPostErrorCode.NOT_EDITABLE);
        }
    }

    private void validateNotBlankIfPresent(String value) {
        if (value != null && value.isBlank()) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }
    }

    private Sort resolveSort(JobPostSortType sortType) {
        return switch (sortType == null ? JobPostSortType.LATEST : sortType) {
            case DEADLINE -> Sort.by(Sort.Direction.ASC, "deadline");
            case BUDGET_HIGH -> Sort.by(Sort.Direction.DESC, "budget");
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
