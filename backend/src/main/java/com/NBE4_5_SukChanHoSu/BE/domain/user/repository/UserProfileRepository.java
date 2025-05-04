package com.NBE4_5_SukChanHoSu.BE.domain.user.repository;


import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    List<UserProfile> findByGender(Gender gender);
    boolean existsByNickName(String nickName);
    boolean existsByUserId(Long userId);
    Optional<UserProfile> findByUserId(Long userId);
}
