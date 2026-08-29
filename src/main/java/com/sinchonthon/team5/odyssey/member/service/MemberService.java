package com.sinchonthon.team5.odyssey.member.service;

import com.sinchonthon.team5.odyssey.global.code.GeneralErrorCode;
import com.sinchonthon.team5.odyssey.global.exception.GeneralException;
import com.sinchonthon.team5.odyssey.member.domain.Member;
import com.sinchonthon.team5.odyssey.member.domain.MemberRole;
import com.sinchonthon.team5.odyssey.member.domain.OwnerProfile;
import com.sinchonthon.team5.odyssey.member.domain.StudentProfile;
import com.sinchonthon.team5.odyssey.member.dto.MemberMeResponse;
import com.sinchonthon.team5.odyssey.member.repository.MemberRepository;
import com.sinchonthon.team5.odyssey.member.repository.OwnerProfileRepository;
import com.sinchonthon.team5.odyssey.member.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OwnerProfileRepository ownerProfileRepository;

    public MemberService(
            MemberRepository memberRepository,
            StudentProfileRepository studentProfileRepository,
            OwnerProfileRepository ownerProfileRepository
    ) {
        this.memberRepository = memberRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.ownerProfileRepository = ownerProfileRepository;
    }

    public MemberMeResponse getMyProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));

        if (member.getRole() == MemberRole.STUDENT) {
            StudentProfile profile = studentProfileRepository.findById(memberId)
                    .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));
            return MemberMeResponse.student(member, profile);
        }

        OwnerProfile profile = ownerProfileRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));
        return MemberMeResponse.owner(member, profile);
    }
}
