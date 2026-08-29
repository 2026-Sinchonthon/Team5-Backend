package com.sinchonthon.team5.odyssey.jobpost.llm;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

record JobPostRefineResult(
        JobPostCategory category,
        String title,
        String refinedDescription,
        boolean parsedByLlm
) {
}
