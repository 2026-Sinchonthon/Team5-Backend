package com.sinchonthon.team5.odyssey.jobapplication.controller;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.jobapplication.JobApplicationSuccessCode;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCancelResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCreateResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicantResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationSummaryResponse;
import com.sinchonthon.team5.odyssey.jobapplication.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Tag(name = "공고 지원", description = "학생 지원 및 사장님 지원자 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping(
            value = "/job-posts/{jobPostId}/applications",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "공고 지원", description = "학생이 공고에 자기소개와 선택 이미지를 첨부해 지원합니다. STUDENT 권한이 필요합니다.")
    public ResponseEntity<ApiResponse<ApplicationCreateResponse>> apply(
            Principal principal,
            @PathVariable Long jobPostId,
            @RequestPart("message")
            @NotBlank(message = "이력 설명 및 자기소개는 필수입니다.")
            @Size(max = 1000, message = "이력 설명 및 자기소개는 1000자 이하여야 합니다.")
            String message,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Long studentId = getMemberId(principal);
        ApplicationCreateResponse response =
                jobApplicationService.apply(
                        studentId,
                        jobPostId,
                        message,
                        image
                );

        return ResponseEntity
                .status(JobApplicationSuccessCode.CREATED.getStatus())
                .body(ApiResponse.onSuccess(
                        JobApplicationSuccessCode.CREATED,
                        response
                ));
    }

    @PostMapping("/applications/{applicationId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "지원 취소", description = "학생이 본인의 대기 중인 지원을 취소합니다.")
    public ResponseEntity<ApiResponse<ApplicationCancelResponse>> cancel(
            Principal principal,
            @PathVariable Long applicationId
    ) {
        Long studentId = getMemberId(principal);
        ApplicationCancelResponse response =
                jobApplicationService.cancel(
                        studentId,
                        applicationId
                );

        return ResponseEntity
                .status(JobApplicationSuccessCode.CANCELED.getStatus())
                .body(ApiResponse.onSuccess(
                        JobApplicationSuccessCode.CANCELED,
                        response
                ));
    }

    @GetMapping("/applications/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "내 지원 목록 조회", description = "학생이 지원한 공고 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ApplicationSummaryResponse>>> getMyApplications(
            Principal principal
    ) {
        Long studentId = getMemberId(principal);
        List<ApplicationSummaryResponse> response =
                jobApplicationService.getMyApplications(studentId);

        return ResponseEntity
                .status(JobApplicationSuccessCode.MY_APPLICATIONS_READ.getStatus())
                .body(ApiResponse.onSuccess(
                        JobApplicationSuccessCode.MY_APPLICATIONS_READ,
                        response
                ));
    }

    @GetMapping("/job-posts/{jobPostId}/applications")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "공고 지원자 목록 조회", description = "사장님이 본인 공고의 지원자를 조회합니다.")
    public ResponseEntity<ApiResponse<List<ApplicantResponse>>> getApplicants(
            Principal principal,
            @PathVariable Long jobPostId
    ) {
        Long ownerId = getMemberId(principal);
        List<ApplicantResponse> response =
                jobApplicationService.getApplicants(ownerId, jobPostId);

        return ResponseEntity
                .status(JobApplicationSuccessCode.APPLICANTS_READ.getStatus())
                .body(ApiResponse.onSuccess(
                        JobApplicationSuccessCode.APPLICANTS_READ,
                        response
                ));
    }

    private Long getMemberId(Principal principal) {
        return Long.valueOf(principal.getName());
    }
}
