package com.sinchonthon.team5.odyssey.submission.controller;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.submission.code.SubmissionSuccessCode;
import com.sinchonthon.team5.odyssey.submission.dto.RevisionRequestCreateRequest;
import com.sinchonthon.team5.odyssey.submission.dto.RevisionRequestCreateResponse;
import com.sinchonthon.team5.odyssey.submission.dto.SubmissionApproveResponse;
import com.sinchonthon.team5.odyssey.submission.dto.SubmissionCreateResponse;
import com.sinchonthon.team5.odyssey.submission.dto.SubmissionHistoryResponse;
import com.sinchonthon.team5.odyssey.submission.service.SubmissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping(
            value = "/matchings/{matchingId}/submissions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<SubmissionCreateResponse>> submit(
            Principal principal,
            @PathVariable Long matchingId,
            @RequestPart(value = "description", required = false)
            @Size(max = 1000, message = "결과물 설명은 1000자 이하여야 합니다.")
            String description,
            @RequestPart("files") List<MultipartFile> files
    ) {
        SubmissionCreateResponse response = submissionService.submit(
                getMemberId(principal),
                matchingId,
                description,
                files
        );

        return ResponseEntity
                .status(SubmissionSuccessCode.CREATED.getStatus())
                .body(ApiResponse.onSuccess(SubmissionSuccessCode.CREATED, response));
    }

    @GetMapping("/matchings/{matchingId}/submissions")
    public ResponseEntity<ApiResponse<List<SubmissionHistoryResponse>>> getHistory(
            Principal principal,
            @PathVariable Long matchingId
    ) {
        List<SubmissionHistoryResponse> response = submissionService.getHistory(
                getMemberId(principal),
                matchingId
        );

        return ResponseEntity
                .status(SubmissionSuccessCode.HISTORY_READ.getStatus())
                .body(ApiResponse.onSuccess(SubmissionSuccessCode.HISTORY_READ, response));
    }

    @PostMapping("/submissions/{submissionId}/revision-requests")
    public ResponseEntity<ApiResponse<RevisionRequestCreateResponse>> requestRevision(
            Principal principal,
            @PathVariable Long submissionId,
            @Valid @RequestBody RevisionRequestCreateRequest request
    ) {
        RevisionRequestCreateResponse response = submissionService.requestRevision(
                getMemberId(principal),
                submissionId,
                request
        );

        return ResponseEntity
                .status(SubmissionSuccessCode.REVISION_CREATED.getStatus())
                .body(ApiResponse.onSuccess(
                        SubmissionSuccessCode.REVISION_CREATED,
                        response
                ));
    }

    @PostMapping("/submissions/{submissionId}/approve")
    public ResponseEntity<ApiResponse<SubmissionApproveResponse>> approve(
            Principal principal,
            @PathVariable Long submissionId
    ) {
        SubmissionApproveResponse response = submissionService.approve(
                getMemberId(principal),
                submissionId
        );

        return ResponseEntity
                .status(SubmissionSuccessCode.APPROVED.getStatus())
                .body(ApiResponse.onSuccess(SubmissionSuccessCode.APPROVED, response));
    }

    private Long getMemberId(Principal principal) {
        return Long.valueOf(principal.getName());
    }
}
