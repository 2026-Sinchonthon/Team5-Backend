package com.sinchonthon.team5.odyssey.submission.repository;

import com.sinchonthon.team5.odyssey.submission.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findAllByMatchingIdOrderByRoundNumberAsc(Long matchingId);

    Optional<Submission> findTopByMatchingIdOrderByRoundNumberDesc(Long matchingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Submission s where s.id = :submissionId")
    Optional<Submission> findByIdForUpdate(@Param("submissionId") Long submissionId);
}
