package com.sinchonthon.team5.odyssey.jobpost.llm;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

record JobPostRefineResult(
        JobPostCategory category,
        String title,
        String refinedDescription,
        String budgetText,
        String deadlineText,
        boolean parsedByLlm
) {

    JobPostRefineResult withBudgetDeadline(String budgetText, String deadlineText) {
        return new JobPostRefineResult(category, title, refinedDescription, budgetText, deadlineText, parsedByLlm);
    }
}
