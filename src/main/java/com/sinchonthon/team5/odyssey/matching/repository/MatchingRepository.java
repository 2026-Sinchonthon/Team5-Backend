package com.sinchonthon.team5.odyssey.matching.repository;

import com.sinchonthon.team5.odyssey.matching.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    boolean existsByJobPostId(Long jobPostId);

    boolean existsByApplicationId(Long applicationId);
}
