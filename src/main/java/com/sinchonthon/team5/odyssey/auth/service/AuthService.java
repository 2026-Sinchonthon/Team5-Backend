package com.sinchonthon.team5.odyssey.auth.service;

import com.sinchonthon.team5.odyssey.auth.dto.request.OwnerSignUpRequest;
import com.sinchonthon.team5.odyssey.auth.dto.request.StudentSignUpRequest;
import com.sinchonthon.team5.odyssey.auth.dto.response.SignUpResponse;
import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.member.code.MemberErrorCode;
import com.sinchonthon.team5.odyssey.member.domain.Member;
import com.sinchonthon.team5.odyssey.member.domain.MemberRole;
import com.sinchonthon.team5.odyssey.member.domain.OwnerProfile;
import com.sinchonthon.team5.odyssey.member.domain.StudentProfile;
import com.sinchonthon.team5.odyssey.member.domain.SupportedUniversity;
import com.sinchonthon.team5.odyssey.member.repository.MemberRepository;
import com.sinchonthon.team5.odyssey.member.repository.OwnerProfileRepository;
import com.sinchonthon.team5.odyssey.member.repository.StudentProfileRepository;
import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OwnerProfileRepository ownerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse signUpStudent(StudentSignUpRequest request) {
        Long universityId = SupportedUniversity.fromEmail(request.email().trim().toLowerCase(Locale.ROOT))
                .map(SupportedUniversity::getId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.UNSUPPORTED_UNIVERSITY_EMAIL));
        Member member = createMember(request.email(), request.password(), request.name(), MemberRole.STUDENT);
        studentProfileRepository.save(new StudentProfile(
                member,
                universityId,
                request.major(),
                request.introduction()
        ));

        return SignUpResponse.from(member);
    }

    public SignUpResponse signUpOwner(OwnerSignUpRequest request) {
        Member member = createMember(request.email(), request.password(), request.name(), MemberRole.OWNER);
        ownerProfileRepository.save(new OwnerProfile(
                member,
                request.businessName(),
                request.address(),
                request.introduction()
        ));

        return SignUpResponse.from(member);
    }

    private Member createMember(String email, String password, String name, MemberRole role) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (memberRepository.existsByEmail(normalizedEmail)) {
            throw new GeneralException(MemberErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Member member = Member.create(normalizedEmail, passwordEncoder.encode(password), name.trim(), role);
        return memberRepository.save(member);
    }
}
