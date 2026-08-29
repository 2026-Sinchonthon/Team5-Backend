package com.sinchonthon.team5.odyssey.matching.repository;

import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;

import java.time.OffsetDateTime;

public interface MatchingDetailProjection {

    Long getMatchingId();

    Long getJobPostId();

    String getTitle();

    String getDescription();

    Long getOwnerId();

    String getOwnerName();

    String getBusinessName();

    Long getStudentId();

    String getStudentName();

    Long getUniversityId();

    String getMajor();

    Integer getAgreedAmount();

    OffsetDateTime getDeadline();

    Integer getRevisionCount();

    Integer getRevisionLimit();

    MatchingStatus getStatus();
}
