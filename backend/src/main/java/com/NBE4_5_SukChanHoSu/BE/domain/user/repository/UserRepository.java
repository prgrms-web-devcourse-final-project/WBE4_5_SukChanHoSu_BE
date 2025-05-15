package com.NBE4_5_SukChanHoSu.BE.domain.user.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
