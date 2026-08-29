package com.sinchonthon.team5.odyssey.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sinchonthon.team5.odyssey.auth.dto.request.StudentSignUpRequest;
import com.sinchonthon.team5.odyssey.auth.dto.request.LoginRequest;
import com.sinchonthon.team5.odyssey.auth.dto.response.LoginResponse;
import com.sinchonthon.team5.odyssey.auth.dto.response.SignUpResponse;
import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.member.domain.MemberRole;
import com.sinchonthon.team5.odyssey.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 학생_회원가입을_하면_회원과_학생_프로필을_저장한다() {
        SignUpResponse response = authService.signUpStudent(new StudentSignUpRequest(
                "student@yonsei.ac.kr",
                "password123!",
                "김학생",
                "컴퓨터과학과",
                "웹 개발을 좋아합니다."
        ));

        assertThat(response.memberId()).isNotNull();
        assertThat(response.role()).isEqualTo(MemberRole.STUDENT);
        assertThat(memberRepository.existsByEmail("student@yonsei.ac.kr")).isTrue();
    }

    @Test
    void 이미_가입된_이메일로는_회원가입할_수_없다() {
        StudentSignUpRequest request = new StudentSignUpRequest(
                "student@yonsei.ac.kr",
                "password123!",
                "김학생",
                null,
                null
        );
        authService.signUpStudent(request);

        assertThatThrownBy(() -> authService.signUpStudent(request))
                .isInstanceOf(GeneralException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }

    @Test
    void 지원하지_않는_대학_도메인으로는_학생_회원가입할_수_없다() {
        StudentSignUpRequest request = new StudentSignUpRequest(
                "student@example.com",
                "password123!",
                "김학생",
                null,
                null
        );

        assertThatThrownBy(() -> authService.signUpStudent(request))
                .isInstanceOf(GeneralException.class)
                .hasMessage("지원하지 않는 대학 이메일입니다.");
    }

    @Test
    void 올바른_이메일과_비밀번호로_로그인하면_엑세스_토큰을_발급한다() {
        authService.signUpStudent(new StudentSignUpRequest(
                "student@yonsei.ac.kr",
                "password123!",
                "김학생",
                null,
                null
        ));

        LoginResponse response = authService.login(new LoginRequest(
                "student@yonsei.ac.kr",
                "password123!"
        ));

        assertThat(response.accessToken()).startsWith("eyJ");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.member().role()).isEqualTo(MemberRole.STUDENT);
    }

    @Test
    void 비밀번호가_일치하지_않으면_로그인할_수_없다() {
        authService.signUpStudent(new StudentSignUpRequest(
                "student@yonsei.ac.kr",
                "password123!",
                "김학생",
                null,
                null
        ));

        assertThatThrownBy(() -> authService.login(new LoginRequest(
                "student@yonsei.ac.kr",
                "wrong-password"
        )))
                .isInstanceOf(GeneralException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
