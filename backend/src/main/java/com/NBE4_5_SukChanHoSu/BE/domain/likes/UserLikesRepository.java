package com.NBE4_5_SukChanHoSu.BE.domain.likes;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikesRepository extends JpaRepository<UserLikes, Long> {

    // 반대쪽 관계도 존재하는지 확인
    boolean existsByFromUserAndToUser(UserProfile fromUser, UserProfile toUser);

    void deleteByFromUserAndToUser(UserProfile toUser, UserProfile fromUser);
}
