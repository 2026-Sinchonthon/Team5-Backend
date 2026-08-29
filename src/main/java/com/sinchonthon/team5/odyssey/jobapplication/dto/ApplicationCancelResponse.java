package com.sinchonthon.team5.odyssey.jobapplication.dto;

import com.sinchonthon.team5.odyssey.jobapplication.enums.JobApplicationStatus;

public record ApplicationCancelResponse(
        Long applicationId,
        JobApplicationStatus status
) {
}
