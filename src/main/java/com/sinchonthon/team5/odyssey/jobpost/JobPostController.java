package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.global.api.PageResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostCreateRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostCreateResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostDetailResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostImageResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostListItemResponse;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostSearchCondition;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostUpdateRequest;
import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/job-posts")
@RequiredArgsConstructor
@Validated
@Tag(name = "공고", description = "외주 공고 등록, 조회, 수정 및 이미지 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "공고 등록", description = "사장님이 공고 정보와 선택 이미지를 multipart/form-data로 등록합니다.")
    public ResponseEntity<ApiResponse<JobPostCreateResponse>> create(
            Principal principal,
            @Valid @RequestPart("request") JobPostCreateRequest request,
            @RequestPart(value = "images", required = false)
            @Size(max = JobPost.MAX_IMAGE_COUNT, message = "이미지는 최대 10장까지 등록할 수 있습니다.")
            List<MultipartFile> images
    ) {
        Long ownerId = getMemberId(principal);
        JobPostCreateResponse response = jobPostService.create(ownerId, request, images);

        return ResponseEntity
                .status(JobPostSuccessCode.CREATED.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.CREATED, response));
    }

    @GetMapping
    @Operation(summary = "공고 목록 조회", description = "카테고리, 상태, 예산, 정렬 조건으로 공고 목록을 페이지 조회합니다.")
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
    @Operation(summary = "내 공고 목록 조회", description = "로그인한 사장님이 등록한 공고 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<JobPostListItemResponse>>> getMyList(
            Principal principal
    ) {
        Long ownerId = getMemberId(principal);
        List<JobPostListItemResponse> response = jobPostService.getMyList(ownerId);

        return ResponseEntity
                .status(JobPostSuccessCode.MY_LIST_FOUND.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.MY_LIST_FOUND, response));
    }

    @GetMapping("/{jobPostId}")
    @Operation(summary = "공고 상세 조회", description = "공고의 상세 정보와 등록된 이미지를 조회합니다.")
    public ResponseEntity<ApiResponse<JobPostDetailResponse>> getDetail(@PathVariable Long jobPostId) {
        JobPostDetailResponse response = jobPostService.getDetail(jobPostId);

        return ResponseEntity
                .status(JobPostSuccessCode.DETAIL_FOUND.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.DETAIL_FOUND, response));
    }

    @PatchMapping("/{jobPostId}")
    @Operation(summary = "공고 수정", description = "사장님이 본인의 OPEN 상태 공고를 수정합니다.")
    public ResponseEntity<ApiResponse<JobPostUpdateResponse>> update(
            Principal principal,
            @PathVariable Long jobPostId,
            @Valid @RequestBody JobPostUpdateRequest request
    ) {
        Long ownerId = getMemberId(principal);
        JobPostUpdateResponse response = jobPostService.update(ownerId, jobPostId, request);

        return ResponseEntity
                .status(JobPostSuccessCode.UPDATED.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.UPDATED, response));
    }

    @DeleteMapping("/{jobPostId}")
    @Operation(summary = "공고 취소", description = "사장님이 본인의 OPEN 상태 공고를 취소합니다.")
    public ResponseEntity<Void> cancel(
            Principal principal,
            @PathVariable Long jobPostId
    ) {
        Long ownerId = getMemberId(principal);
        jobPostService.cancel(ownerId, jobPostId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/{jobPostId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "공고 이미지 추가", description = "사장님이 본인 공고에 이미지를 한 장 추가합니다.")
    public ResponseEntity<ApiResponse<JobPostImageResponse>> addImage(
            Principal principal,
            @PathVariable Long jobPostId,
            @RequestPart("image") MultipartFile image
    ) {
        Long ownerId = getMemberId(principal);
        JobPostImageResponse response = jobPostService.addImage(ownerId, jobPostId, image);

        return ResponseEntity
                .status(JobPostSuccessCode.IMAGE_ADDED.getStatus())
                .body(ApiResponse.onSuccess(JobPostSuccessCode.IMAGE_ADDED, response));
    }

    @DeleteMapping("/{jobPostId}/images/{imageId}")
    @Operation(summary = "공고 이미지 삭제", description = "사장님이 본인 공고에서 이미지를 삭제합니다.")
    public ResponseEntity<Void> removeImage(
            Principal principal,
            @PathVariable Long jobPostId,
            @PathVariable Long imageId
    ) {
        Long ownerId = getMemberId(principal);
        jobPostService.removeImage(ownerId, jobPostId, imageId);

        return ResponseEntity.noContent().build();
    }

    private Long getMemberId(Principal principal) {
        return Long.valueOf(principal.getName());
    }
}
