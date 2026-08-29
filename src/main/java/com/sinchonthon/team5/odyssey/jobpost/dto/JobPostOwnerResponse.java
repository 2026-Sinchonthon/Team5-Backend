package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.member.domain.OwnerProfile;

public record JobPostOwnerResponse(
        Long ownerId,
        String businessName,
        String address
) {

    public static JobPostOwnerResponse from(OwnerProfile ownerProfile) {
        return new JobPostOwnerResponse(
                ownerProfile.getMemberId(),
                ownerProfile.getBusinessName(),
                ownerProfile.getAddress()
        );
    }
}
