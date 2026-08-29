package com.sinchonthon.team5.odyssey.auth.dto.response;

import com.sinchonthon.team5.odyssey.member.domain.Member;
import com.sinchonthon.team5.odyssey.member.domain.MemberRole;

public record SignUpResponse(Long memberId, String email, String name, MemberRole role) {

    public static SignUpResponse from(Member member) {
        return new SignUpResponse(member.getId(), member.getEmail(), member.getName(), member.getRole());
    }
}
