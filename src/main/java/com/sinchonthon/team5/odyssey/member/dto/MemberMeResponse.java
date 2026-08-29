package com.sinchonthon.team5.odyssey.member.dto;

import com.sinchonthon.team5.odyssey.member.domain.Member;
import com.sinchonthon.team5.odyssey.member.domain.MemberRole;
import com.sinchonthon.team5.odyssey.member.domain.MemberStatus;
import com.sinchonthon.team5.odyssey.member.domain.OwnerProfile;
import com.sinchonthon.team5.odyssey.member.domain.StudentProfile;
import com.sinchonthon.team5.odyssey.member.domain.SupportedUniversity;

public record MemberMeResponse(
        Long memberId,
        String email,
        String name,
        MemberRole role,
        MemberStatus status,
        Object profile
) {
    public static MemberMeResponse student(Member member, StudentProfile profile) {
        String universityName = SupportedUniversity.fromId(profile.getUniversityId())
                .map(SupportedUniversity::getName)
                .orElse("알 수 없는 대학교");
        return new MemberMeResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole(),
                member.getStatus(),
                new StudentProfileResponse(
                        profile.getUniversityId(),
                        universityName,
                        profile.getMajor(),
                        profile.getIntroduction()
                )
        );
    }

    public static MemberMeResponse owner(Member member, OwnerProfile profile) {
        return new MemberMeResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole(),
                member.getStatus(),
                new OwnerProfileResponse(
                        profile.getBusinessName(),
                        profile.getAddress(),
                        profile.getIntroduction()
                )
        );
    }

    public record StudentProfileResponse(
            Long universityId,
            String universityName,
            String major,
            String introduction
    ) {
    }

    public record OwnerProfileResponse(
            String businessName,
            String address,
            String introduction
    ) {
    }
}
