package com.sinchonthon.team5.odyssey.jobapplication.repository;

import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;

import java.time.OffsetDateTime;

public interface ApplicantProjection {

    Long getApplicationId();

    Long getMemberId();

    String getName();

    Long getUniversityId();

    String getMajor();

    String getIntroduction();

    String getMessage();

    JobApplicationStatus getStatus();

    OffsetDateTime getAppliedAt();
}
