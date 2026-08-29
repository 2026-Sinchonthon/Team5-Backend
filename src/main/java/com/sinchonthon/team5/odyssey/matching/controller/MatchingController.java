package com.sinchonthon.team5.odyssey.matching.controller;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.matching.MatchingSuccessCode;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingAcceptRequest;
import com.sinchonthon.team5.odyssey.matching.dto.MatchingCreateResponse;
import com.sinchonthon.team5.odyssey.matching.service.MatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/applications/{applicationId}/accept")
    public ResponseEntity<ApiResponse<MatchingCreateResponse>> accept(
            @RequestHeader("X-Member-Id") Long ownerId,
            @PathVariable Long applicationId,
            @Valid @RequestBody MatchingAcceptRequest request
    ) {
        MatchingCreateResponse response =
                matchingService.accept(ownerId, applicationId, request);

        return ResponseEntity
                .status(MatchingSuccessCode.CREATED.getStatus())
                .body(ApiResponse.onSuccess(MatchingSuccessCode.CREATED, response));
    }
}
