package com.NBE4_5_SukChanHoSu.BE.domain.admin.service;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.UserDetailResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserStatus;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUpdateUserStatus_ValidUser_ChangeStatus() {
        // Given
        User user = userRepository.findByEmail("initUser1@example.com");
        Long userId = user.getId();
//        UserStatus initialStatus = user.getStatus();
        UserStatus targetStatus = UserStatus.SUSPENDED;

        // When
        adminService.updateUserStatus(userId, targetStatus);

        // Then
        User updatedUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("업데이트된 유저 없음"));
        assertEquals(targetStatus, updatedUser.getStatus());
    }

    @Test
    void testUpdateUserStatus_UserDeleted() {
        // Given
        User user = userRepository.findByEmail("initUser2@example.com");
        Long userId = user.getId();

        // When
        adminService.updateUserStatus(userId, UserStatus.DELETED);

        // Then
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void testGetUserDetail_ValidUser() {
        // Given
        User user = userRepository.findByEmail("initUser4@example.com");
        Long userId = user.getId();

        // When
        UserDetailResponse response = adminService.getUserDetail(userId);

        // Then
        assertEquals(userId, response.getId());
        assertEquals("initUser4@example.com", response.getEmail());
        assertEquals(UserStatus.ACTIVE, response.getStatus());
    }

    @Test
    void testGetUserDetail_UserNotFound() {
        // Given
        Long nonExistingUserId = 999L;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.getUserDetail(nonExistingUserId);
        });
        assertEquals("존재하지 않는 유저입니다.", exception.getMessage());
    }
}