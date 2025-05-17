package com.NBE4_5_SukChanHoSu.BE.domain.admin.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.StatusUpdateRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.UserDetailResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.service.AdminService;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.service.AdminMonitoringService;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "관리자 기능", description = "사용자 관리 (정지/탈퇴/활성화) 및 상세 조회")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AdminMonitoringService adminMonitoringService;
    private final ReviewService reviewService;
    @Operation(summary = "사용자 상태 변경", description = "관리자가 사용자의 상태 (ACTIVE, SUSPENDED, DELETED)를 변경합니다.")
    @PatchMapping("/users/{userId}/status")
    public RsData<String> updateStatus(
            @PathVariable Long userId,
            @RequestBody StatusUpdateRequest request
    ) {
        adminService.updateUserStatus(userId, request.getStatus());
        return new RsData<>("200-OK", "사용자 상태가 성공적으로 변경되었습니다.", request.getStatus().name());
    }

    @Operation(summary = "사용자 상세 조회", description = "관리자가 사용자 상세 정보를 조회합니다.")
    @GetMapping("/users/{userId}")
    public RsData<UserDetailResponse> getUserDetail(@PathVariable Long userId) {
        UserDetailResponse response = adminService.getUserDetail(userId);
        return new RsData<>("200-OK", "사용자 상세 정보 조회 성공", response);
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "일일 매칭 수 조회", description = "오늘 일어난 매칭 수를 조회합니다.")
    @GetMapping("/daily-matches")
    public RsData<Long> getDailyMatches() {
        long count = adminMonitoringService.getTodayDailyMatches();
        return new RsData<>("200-OK", "오늘 일어난 매칭 수 조회 성공", count);
    }

    @Operation(summary = "부적절 리뷰 삭제", description = "관리자가 리뷰를 삭제합니다.")
    @DeleteMapping("/reviews/{reviewId}")
    public RsData<String> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new RsData<>("200-OK", "리뷰가 성공적으로 삭제되었습니다.", "리뷰 ID: " + reviewId);
    }

}

