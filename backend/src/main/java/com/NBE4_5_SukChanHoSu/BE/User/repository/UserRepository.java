package com.NBE4_5_SukChanHoSu.BE.User.repository;

import com.NBE4_5_SukChanHoSu.BE.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);
}
