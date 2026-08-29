package com.sinchonthon.team5.odyssey.member.controller;

import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.member.code.MemberSuccessCode;
import com.sinchonthon.team5.odyssey.member.dto.MemberMeResponse;
import com.sinchonthon.team5.odyssey.member.service.MemberService;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberMeResponse>> getMyProfile(Principal principal) {
        MemberMeResponse response = memberService.getMyProfile(Long.valueOf(principal.getName()));
        return ResponseEntity.status(MemberSuccessCode.MY_PROFILE_READ.getStatus())
                .body(ApiResponse.onSuccess(MemberSuccessCode.MY_PROFILE_READ, response));
    }
}
