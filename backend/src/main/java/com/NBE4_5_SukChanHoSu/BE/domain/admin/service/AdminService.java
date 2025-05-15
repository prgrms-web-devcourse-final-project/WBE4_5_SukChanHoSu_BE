package com.NBE4_5_SukChanHoSu.BE.domain.admin.service;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.UserDetailResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserStatus;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
//import com.NBE4_5_SukChanHoSu.BE.global.filter.InappropriateContentFilter;
//import com.NBE4_5_SukChanHoSu.BE.global.filter.ProfanityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ReviewService reviewService;
//    private final ProfanityFilter profanityFilter;

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

    @Transactional
    public void deleteInappropriateReview(Long reviewId, String reason) throws Exception {
        ReviewResponseDto review = reviewService.getOneReview(reviewId);
        String content = review.getContent();

        // 두 필터 중 하나라도 부적절하다고 판단하면 삭제 처리
//        if (profanityFilter.containsProfanity(content) || InappropriateContentFilter.isInappropriate(content)) {
//            reviewService.deleteReview(review.getId());
//            System.out.println("리뷰 삭제됨 - ID: " + reviewId + ", 이유: " + reason);
//        } else {
//            throw new IllegalStateException("부적절하지 않은 리뷰입니다.");
//        }
    }
}