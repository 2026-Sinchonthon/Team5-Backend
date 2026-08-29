package com.sinchonthon.team5.odyssey.matching.controller;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.matching.MatchingSuccessCode;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingAcceptRequest;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingCreateResponse;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingDetailResponse;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingSummaryResponse;
import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;
import com.sinchonthon.team5.odyssey.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "매칭", description = "지원 수락 및 작업 매칭 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/applications/{applicationId}/accept")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "지원 수락 및 매칭 생성", description = "사장님이 지원을 수락해 매칭을 생성합니다. OWNER 권한이 필요합니다.")
    public ResponseEntity<ApiResponse<MatchingCreateResponse>> accept(
            Principal principal,
            @PathVariable Long applicationId,
            @Valid @RequestBody MatchingAcceptRequest request
    ) {
        Long ownerId = getMemberId(principal);
        MatchingCreateResponse response =
                matchingService.accept(ownerId, applicationId, request);

        return ResponseEntity
                .status(MatchingSuccessCode.CREATED.getStatus())
                .body(ApiResponse.onSuccess(MatchingSuccessCode.CREATED, response));
    }

    @GetMapping("/matchings/me")
    @Operation(summary = "내 매칭 목록 조회", description = "로그인 회원이 참여 중인 매칭 목록을 상태별로 조회합니다.")
    public ResponseEntity<ApiResponse<List<MatchingSummaryResponse>>> getMyMatchings(
            Principal principal,
            @RequestParam(required = false) MatchingStatus status
    ) {
        Long memberId = getMemberId(principal);
        List<MatchingSummaryResponse> response =
                matchingService.getMyMatchings(memberId, status);

        return ResponseEntity
                .status(MatchingSuccessCode.LIST_READ.getStatus())
                .body(ApiResponse.onSuccess(MatchingSuccessCode.LIST_READ, response));
    }

    @GetMapping("/matchings/{matchingId}")
    @Operation(summary = "매칭 상세 조회", description = "매칭 참여자인 학생 또는 사장님만 상세 정보를 조회할 수 있습니다.")
    public ResponseEntity<ApiResponse<MatchingDetailResponse>> getDetail(
            Principal principal,
            @PathVariable Long matchingId
    ) {
        Long memberId = getMemberId(principal);
        MatchingDetailResponse response =
                matchingService.getDetail(memberId, matchingId);

        return ResponseEntity
                .status(MatchingSuccessCode.DETAIL_READ.getStatus())
                .body(ApiResponse.onSuccess(MatchingSuccessCode.DETAIL_READ, response));
    }

    private Long getMemberId(Principal principal) {
        return Long.valueOf(principal.getName());
    }
}
