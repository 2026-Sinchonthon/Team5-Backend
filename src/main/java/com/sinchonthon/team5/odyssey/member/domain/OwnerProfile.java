package com.sinchonthon.team5.odyssey.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "owner_profiles")
public class OwnerProfile {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(length = 255)
    private String address;

    @Column(length = 500)
    private String introduction;


    public OwnerProfile(Member member, String businessName, String address, String introduction) {
        this.member = member;
        this.businessName = businessName;
        this.address = address;
        this.introduction = introduction;
    }
}
