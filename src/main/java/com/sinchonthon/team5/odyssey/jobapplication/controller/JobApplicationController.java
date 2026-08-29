package com.sinchonthon.team5.odyssey.jobapplication.controller;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.jobapplication.JobApplicationSuccessCode;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCancelResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCreateRequest;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationCreateResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicantResponse;
import com.sinchonthon.team5.odyssey.jobapplication.dto.ApplicationSummaryResponse;
import com.sinchonthon.team5.odyssey.jobapplication.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping("/job-posts/{jobPostId}/applications")
    public ResponseEntity<ApiResponse<ApplicationCreateResponse>> apply(
            @RequestHeader("X-Member-Id") Long studentId,
            @PathVariable Long jobPostId,
            @Valid @RequestBody ApplicationCreateRequest request
    ) {
        ApplicationCreateResponse response =
                jobApplicationService.apply(
                        studentId,
                        jobPostId,
                        request
                );

        return ResponseEntity
                .status(JobApplicationSuccessCode.CREATED.getStatus())
                .body(ApiResponse.onSuccess(
                        JobApplicationSuccessCode.CREATED,
                        response
                ));
    }

    @PostMapping("/applications/{applicationId}/cancel")
    public ResponseEntity<ApiResponse<ApplicationCancelResponse>> cancel(
            @RequestHeader("X-Member-Id") Long studentId,
            @PathVariable Long applicationId
    ) {
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
    public ResponseEntity<ApiResponse<List<ApplicationSummaryResponse>>> getMyApplications(
            @RequestHeader("X-Member-Id") Long studentId
    ) {
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
    public ResponseEntity<ApiResponse<List<ApplicantResponse>>> getApplicants(
            @RequestHeader("X-Member-Id") Long ownerId,
            @PathVariable Long jobPostId
    ) {
        List<ApplicantResponse> response =
                jobApplicationService.getApplicants(ownerId, jobPostId);

        return ResponseEntity
                .status(JobApplicationSuccessCode.APPLICANTS_READ.getStatus())
                .body(ApiResponse.onSuccess(
                        JobApplicationSuccessCode.APPLICANTS_READ,
                        response
                ));
    }
}
