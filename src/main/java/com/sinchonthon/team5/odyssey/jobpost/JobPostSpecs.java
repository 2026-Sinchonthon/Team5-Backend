package com.sinchonthon.team5.odyssey.jobpost;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostStatus;

import org.springframework.data.jpa.domain.Specification;

public final class JobPostSpecs {

    private JobPostSpecs() {
    }

    public static Specification<JobPost> categoryEquals(JobPostCategory category) {
        return (root, query, cb) -> category == null
                ? null
                : cb.equal(root.get("category"), category);
    }

    public static Specification<JobPost> statusEquals(JobPostStatus status) {
        return (root, query, cb) -> status == null
                ? null
                : cb.equal(root.get("status"), status);
    }

    public static Specification<JobPost> budgetGreaterThanOrEqual(Integer minBudget) {
        return (root, query, cb) -> minBudget == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("budget"), minBudget);
    }

    public static Specification<JobPost> budgetLessThanOrEqual(Integer maxBudget) {
        return (root, query, cb) -> maxBudget == null
                ? null
                : cb.lessThanOrEqualTo(root.get("budget"), maxBudget);
    }
}
