package com.sinchonthon.team5.odyssey.auth.controller;

import com.sinchonthon.team5.odyssey.auth.code.AuthSuccessCode;
import com.sinchonthon.team5.odyssey.auth.dto.request.OwnerSignUpRequest;
import com.sinchonthon.team5.odyssey.auth.dto.request.StudentSignUpRequest;
import com.sinchonthon.team5.odyssey.auth.dto.request.LoginRequest;
import com.sinchonthon.team5.odyssey.auth.dto.response.LoginResponse;
import com.sinchonthon.team5.odyssey.auth.dto.response.SignUpResponse;
import com.sinchonthon.team5.odyssey.auth.service.AuthService;
import com.sinchonthon.team5.odyssey.global.api.ApiResponse;
import com.sinchonthon.team5.odyssey.global.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "인증", description = "회원가입 및 JWT 로그인 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup/student")
    @Operation(summary = "학생 회원가입", description = "대학 이메일 도메인으로 소속 대학을 판별해 학생 회원과 프로필을 생성합니다.", security = {})
    public ResponseEntity<ApiResponse<SignUpResponse>> signUpStudent(
            @Valid @RequestBody StudentSignUpRequest request
    ) {
        SignUpResponse response = authService.signUpStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response));
    }

    @PostMapping("/signup/owner")
    @Operation(summary = "사장님 회원가입", description = "사장님 회원과 매장 프로필을 생성합니다.", security = {})
    public ResponseEntity<ApiResponse<SignUpResponse>> signUpOwner(
            @Valid @RequestBody OwnerSignUpRequest request
    ) {
        SignUpResponse response = authService.signUpOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 검증하고 JWT Access Token을 발급합니다.", security = {})
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(AuthSuccessCode.LOGIN_SUCCESS, authService.login(request)));
    }
}
