package com.sinchonthon.team5.odyssey.jobpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {

    List<JobPost> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
