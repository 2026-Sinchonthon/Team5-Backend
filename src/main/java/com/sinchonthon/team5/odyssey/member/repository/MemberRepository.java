package com.sinchonthon.team5.odyssey.member.repository;

import com.sinchonthon.team5.odyssey.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);
}
