package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.global.api.PageResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostCreateRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostCreateResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostDetailResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostImageAddRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostImageResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostListItemResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostSearchCondition;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostUpdateRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostUpdateResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-posts")
@RequiredArgsConstructor
@Validated
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobPostCreateResponse>> create(
            @RequestHeader("X-Member-Id") Long ownerId,
            @Valid @RequestBody JobPostCreateRequest request
    ) {
        JobPostCreateResponse response = jobPostService.create(ownerId, request);

        return ResponseEntity
                .status(JobPostSuccessCode.CREATED.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.CREATED, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobPostListItemResponse>>> getList(
            @ModelAttribute JobPostSearchCondition condition,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        PageResponse<JobPostListItemResponse> response = jobPostService.getList(condition, page, size);

        return ResponseEntity
                .status(JobPostSuccessCode.LIST_FOUND.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.LIST_FOUND, response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<JobPostListItemResponse>>> getMyList(
            @RequestHeader("X-Member-Id") Long ownerId
    ) {
        List<JobPostListItemResponse> response = jobPostService.getMyList(ownerId);

        return ResponseEntity
                .status(JobPostSuccessCode.MY_LIST_FOUND.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.MY_LIST_FOUND, response));
    }

    @GetMapping("/{jobPostId}")
    public ResponseEntity<ApiResponse<JobPostDetailResponse>> getDetail(@PathVariable Long jobPostId) {
        JobPostDetailResponse response = jobPostService.getDetail(jobPostId);

        return ResponseEntity
                .status(JobPostSuccessCode.DETAIL_FOUND.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.DETAIL_FOUND, response));
    }

    @PatchMapping("/{jobPostId}")
    public ResponseEntity<ApiResponse<JobPostUpdateResponse>> update(
            @RequestHeader("X-Member-Id") Long ownerId,
            @PathVariable Long jobPostId,
            @Valid @RequestBody JobPostUpdateRequest request
    ) {
        JobPostUpdateResponse response = jobPostService.update(ownerId, jobPostId, request);

        return ResponseEntity
                .status(JobPostSuccessCode.UPDATED.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.UPDATED, response));
    }

    @DeleteMapping("/{jobPostId}")
    public ResponseEntity<Void> cancel(
            @RequestHeader("X-Member-Id") Long ownerId,
            @PathVariable Long jobPostId
    ) {
        jobPostService.cancel(ownerId, jobPostId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobPostId}/images")
    public ResponseEntity<ApiResponse<JobPostImageResponse>> addImage(
            @RequestHeader("X-Member-Id") Long ownerId,
            @PathVariable Long jobPostId,
            @Valid @RequestBody JobPostImageAddRequest request
    ) {
        JobPostImageResponse response = jobPostService.addImage(ownerId, jobPostId, request);

        return ResponseEntity
                .status(JobPostSuccessCode.IMAGE_ADDED.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.IMAGE_ADDED, response));
    }

    @DeleteMapping("/{jobPostId}/images/{imageId}")
    public ResponseEntity<Void> removeImage(
            @RequestHeader("X-Member-Id") Long ownerId,
            @PathVariable Long jobPostId,
            @PathVariable Long imageId
    ) {
        jobPostService.removeImage(ownerId, jobPostId, imageId);

        return ResponseEntity.noContent().build();
    }
}
