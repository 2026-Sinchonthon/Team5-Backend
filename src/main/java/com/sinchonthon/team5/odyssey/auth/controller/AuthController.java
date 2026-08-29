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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup/student")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUpStudent(
            @Valid @RequestBody StudentSignUpRequest request
    ) {
        SignUpResponse response = authService.signUpStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response));
    }

    @PostMapping("/signup/owner")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUpOwner(
            @Valid @RequestBody OwnerSignUpRequest request
    ) {
        SignUpResponse response = authService.signUpOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(AuthSuccessCode.LOGIN_SUCCESS, authService.login(request)));
    }
}
