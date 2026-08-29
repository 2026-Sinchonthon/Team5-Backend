package com.sinchonthon.team5.odyssey.matching.repository;

import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;

import java.time.OffsetDateTime;

public interface MatchingSummaryProjection {

    Long getMatchingId();

    Long getJobPostId();

    String getTitle();

    Integer getAgreedAmount();

    OffsetDateTime getDeadline();

    Integer getRevisionCount();

    Integer getRevisionLimit();

    MatchingStatus getStatus();
}
