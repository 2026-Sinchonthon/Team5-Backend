package com.sinchonthon.team5.odyssey.matching.repository;

import com.sinchonthon.team5.odyssey.matching.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import com.sinchonthon.team5.odyssey.matching.enums.MatchingStatus;

import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    boolean existsByJobPostId(Long jobPostId);

    boolean existsByApplicationId(Long applicationId);

    @Query("""
            select
                m.id as matchingId,
                j.id as jobPostId,
                j.title as title,
                op.businessName as businessName,
                image.imageUrl as thumbnailImageUrl,
                m.agreedAmount as agreedAmount,
                m.deadline as deadline,
                m.revisionCount as revisionCount,
                j.revisionLimit as revisionLimit,
                m.status as status
            from Matching m
            join JobPost j on j.id = m.jobPostId
            join JobApplication a on a.id = m.applicationId
            join OwnerProfile op on op.memberId = j.ownerId
            left join j.images image on image.sortOrder = 0
            where (j.ownerId = :memberId or a.studentId = :memberId)
              and (:status is null or m.status = :status)
            order by m.matchedAt desc
            """)
    List<MatchingSummaryProjection> findSummariesByMemberIdAndStatus(
            @Param("memberId") Long memberId,
            @Param("status") MatchingStatus status
    );

    @Query("""
            select
                m.id as matchingId,
                j.id as jobPostId,
                j.title as title,
                j.description as description,
                owner.id as ownerId,
                owner.name as ownerName,
                op.businessName as businessName,
                student.id as studentId,
                student.name as studentName,
                sp.universityId as universityId,
                sp.major as major,
                m.agreedAmount as agreedAmount,
                m.deadline as deadline,
                m.revisionCount as revisionCount,
                j.revisionLimit as revisionLimit,
                m.status as status
            from Matching m
            join JobPost j on j.id = m.jobPostId
            join JobApplication a on a.id = m.applicationId
            join Member owner on owner.id = j.ownerId
            join OwnerProfile op on op.memberId = owner.id
            join Member student on student.id = a.studentId
            join StudentProfile sp on sp.memberId = student.id
            where m.id = :matchingId
            """)
    Optional<MatchingDetailProjection> findDetailById(
            @Param("matchingId") Long matchingId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Matching m where m.id = :matchingId")
    Optional<Matching> findByIdForUpdate(@Param("matchingId") Long matchingId);
}
