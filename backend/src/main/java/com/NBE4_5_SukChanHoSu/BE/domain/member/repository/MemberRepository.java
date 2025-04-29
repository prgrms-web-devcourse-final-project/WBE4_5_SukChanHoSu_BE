package com.NBE4_5_SukChanHoSu.BE.domain.member.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
}
