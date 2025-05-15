package com.NBE4_5_SukChanHoSu.BE.domain.admin.service;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.UserDetailResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserStatus;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateUserStatus_ValidUser_ChangeStatus() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setStatus(UserStatus.ACTIVE);

        // When
        adminService.updateUserStatus(1L, UserStatus.SUSPENDED);

        // Then
        assertEquals(UserStatus.SUSPENDED, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserStatus_UserDeleted() {
        // Given
        User user = new User();
        user.setId(2L);
        user.setStatus(UserStatus.ACTIVE);

        // When
        adminService.updateUserStatus(2L, UserStatus.DELETED);

        // Then
        verify(userRepository).deleteById(2L);
    }

    @Test
    void testGetUserDetail_ValidUser() {
        // Given
        User user = new User();
        user.setId(4L);
        user.setEmail("initUser4@example.com");
        user.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));

        // When
        UserDetailResponse response = adminService.getUserDetail(4L);

        // Then
        assertEquals(4L, response.getId());
        assertEquals("initUser4@example.com", response.getEmail());
        assertEquals(UserStatus.ACTIVE, response.getStatus());
    }

    @Test
    void testGetUserDetail_UserNotFound() {

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.getUserDetail(5L);
        });
        assertEquals("존재하지 않는 유저입니다.", exception.getMessage());
    }
}