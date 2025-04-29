package com.NBE4_5_SukChanHoSu.BE.domain.user.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByNickName(String nickName);
}
