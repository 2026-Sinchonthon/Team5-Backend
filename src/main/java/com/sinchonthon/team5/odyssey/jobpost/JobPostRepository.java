package com.sinchonthon.team5.odyssey.jobpost;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {

    List<JobPost> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select j from JobPost j where j.id = :id")
    Optional<JobPost> findByIdForUpdate(@Param("id") Long id);
}
