package com.NBE4_5_SukChanHoSu.BE.domain.likes;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {
    // 매칭 정보 찾기
    List<Matching> findByUser1OrUser2(UserProfile user1, UserProfile user2);
}
