package com.sinchonthon.team5.odyssey.jobpost.llm;

import com.sinchonthon.team5.odyssey.jobpost.dto.JobPostRefineResponse;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobPostRefineService {

    private final JobPostRefineParser refineParser;

    public JobPostRefineResponse refine(String rawRequest, JobPostCategory category) {
        JobPostRefineResult result = refineParser.parse(rawRequest, category);

        return new JobPostRefineResponse(result.refinedDescription(), rawRequest);
    }
}
