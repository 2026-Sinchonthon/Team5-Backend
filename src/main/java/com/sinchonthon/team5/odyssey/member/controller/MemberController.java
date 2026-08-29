package com.sinchonthon.team5.odyssey.member.controller;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.member.code.MemberSuccessCode;
import com.sinchonthon.team5.odyssey.member.dto.MemberMeResponse;
import com.sinchonthon.team5.odyssey.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "회원 / 프로필", description = "로그인 회원의 프로필 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "JWT의 회원 ID로 학생 또는 사장님 프로필을 조회합니다.")
    public ResponseEntity<ApiResponse<MemberMeResponse>> getMyProfile(Principal principal) {
        MemberMeResponse response = memberService.getMyProfile(Long.valueOf(principal.getName()));
        return ResponseEntity.status(MemberSuccessCode.MY_PROFILE_READ.getStatus())
                .body(ApiResponse.onSuccess(MemberSuccessCode.MY_PROFILE_READ, response));
    }
}
