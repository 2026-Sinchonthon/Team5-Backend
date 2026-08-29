package com.sinchonthon.team5.odyssey.jobpost.dto;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostSortType;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

public record JobPostSearchCondition(
        JobPostCategory category,
        JobPostStatus status,
        Integer minBudget,
        Integer maxBudget,
        JobPostSortType sort
) {
}
