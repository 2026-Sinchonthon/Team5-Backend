package com.sinchonthon.team5.odyssey.jobpost.llm;

import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostRefineResponse;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class JobPostRefineService {

    private final JobPostRefineParser refineParser;
    private final JobPostDeadlineParser deadlineParser;

    public JobPostRefineResponse refine(String rawRequest) {
        JobPostCategory category = refineParser.classifyCategory(rawRequest);
        JobPostRefineResult result = refineParser.parse(rawRequest, category);

        Integer budget = refineParser.parseBudgetAmount(result.budgetText());
        var deadline = deadlineParser.parse(result.deadlineText(), LocalDate.now(ZoneOffset.ofHours(9)));

        return new JobPostRefineResponse(
                result.title(),
                result.refinedDescription(),
                result.category(),
                budget,
                result.budgetText(),
                deadline,
                result.deadlineText(),
                rawRequest
        );
    }
}
