package com.sinchonthon.team5.odyssey.submission.repository;

import com.sinchonthon.team5.odyssey.submission.domain.RevisionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RevisionRequestRepository extends JpaRepository<RevisionRequest, Long> {

    boolean existsBySubmissionId(Long submissionId);

    Optional<RevisionRequest> findBySubmissionId(Long submissionId);

    List<RevisionRequest> findAllBySubmissionIdIn(Collection<Long> submissionIds);
}
