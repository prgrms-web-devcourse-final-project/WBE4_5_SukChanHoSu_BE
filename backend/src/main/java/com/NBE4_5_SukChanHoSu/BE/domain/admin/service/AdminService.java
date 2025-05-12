package com.NBE4_5_SukChanHoSu.BE.domain.admin.service;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.AdminStatisticsResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.UserDetailResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserStatus;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.RecommendUserRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RecommendUserRepository recommendUserRepository;

    public void updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        // 상태가 'DELETED'이면 유저 삭제
        if (status == UserStatus.DELETED) {
            userRepository.deleteById(userId);  // 유저 삭제
        } else {
            user.setStatus(status);
            userRepository.save(user);  // 상태 업데이트
        }
    }

    public UserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }

    public AdminStatisticsResponse getStatistics() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        // 내일의 시작 시간 (00:00:00) (하루 범위)
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        int dailyMatches = recommendUserRepository.countDailyMatches(startOfDay, endOfDay);
        int totalUsers = userRepository.countAllUsers();

        return new AdminStatisticsResponse(dailyMatches, totalUsers);
    }
}