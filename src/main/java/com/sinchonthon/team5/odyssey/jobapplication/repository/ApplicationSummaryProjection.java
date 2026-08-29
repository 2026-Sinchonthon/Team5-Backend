package com.sinchonthon.team5.odyssey.jobapplication.repository;

import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;

import java.time.OffsetDateTime;

public interface ApplicationSummaryProjection {

    Long getApplicationId();

    Long getJobPostId();

    String getTitle();

    String getBusinessName();

    Integer getBudget();

    OffsetDateTime getDeadline();

    JobApplicationStatus getStatus();

    OffsetDateTime getAppliedAt();
}
