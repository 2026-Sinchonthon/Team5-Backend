package com.sinchonthon.team5.odyssey.auth.dto.response;

import com.sinchonthon.team5.odyssey.member.domain.Member;
import com.sinchonthon.team5.odyssey.member.domain.MemberRole;

public record LoginResponse(String accessToken, String tokenType, LoginMember member) {

    public static LoginResponse of(String accessToken, Member member) {
        return new LoginResponse(accessToken, "Bearer", LoginMember.from(member));
    }

    public record LoginMember(Long memberId, String name, MemberRole role) {

        private static LoginMember from(Member member) {
            return new LoginMember(member.getId(), member.getName(), member.getRole());
        }
    }
}
