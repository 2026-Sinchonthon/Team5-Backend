package com.sinchonthon.team5.odyssey.member.repository;

import com.sinchonthon.team5.odyssey.member.domain.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
}
