package com.sinchonthon.team5.odyssey.submission.repository;

import com.sinchonthon.team5.odyssey.submission.domain.SubmissionFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SubmissionFileRepository extends JpaRepository<SubmissionFile, Long> {

    List<SubmissionFile> findAllBySubmissionIdInOrderByIdAsc(
            Collection<Long> submissionIds
    );
}
