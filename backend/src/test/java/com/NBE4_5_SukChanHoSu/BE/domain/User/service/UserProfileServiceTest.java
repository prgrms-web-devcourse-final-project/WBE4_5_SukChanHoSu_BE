package com.NBE4_5_SukChanHoSu.BE.domain.User.service;

import com.NBE4_5_SukChanHoSu.BE.User.dto.ProfileRequestDto;
import com.NBE4_5_SukChanHoSu.BE.User.dto.ProfileUpdateRequestDto;
import com.NBE4_5_SukChanHoSu.BE.User.dto.UserProfileDto;
import com.NBE4_5_SukChanHoSu.BE.User.entity.User;
import com.NBE4_5_SukChanHoSu.BE.User.enums.Gender;
import com.NBE4_5_SukChanHoSu.BE.User.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.User.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
    }

    @Test
    void createProfile_성공() {
        // given
        ProfileRequestDto dto = ProfileRequestDto.builder().nickname("testnick").email("test@example.com").gender(Gender.MALE).latitude(37.5).longitude(127.0).birthdate(LocalDate.of(1995, 5, 5)).profileImage("image.jpg").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        userProfileService.createProfile(1L, dto);

        // then
        assertThat(user.getNickname()).isEqualTo("testnick");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(user);
    }

    @Test
    void createProfile_이미등록된프로필_실패() {
        // given
        user.setNickname("alreadySet");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ProfileRequestDto dto = ProfileRequestDto.builder().nickname("newNick").build();

        // when & then
        assertThatThrownBy(() -> userProfileService.createProfile(1L, dto)).isInstanceOf(IllegalStateException.class).hasMessage("이미 프로필이 등록된 사용자입니다.");
    }

    @Test
    void updateProfile_성공() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ProfileUpdateRequestDto dto = ProfileUpdateRequestDto.builder().nickname("updatedNick").gender(Gender.FEMALE).latitude(36.5).longitude(128.0).birthdate(LocalDate.of(2000, 1, 1)).profileImage("newImage.jpg").build();

        // when
        userProfileService.updateProfile(1L, dto);

        // then
        assertThat(user.getNickname()).isEqualTo("updatedNick");
        verify(userRepository).save(user);
    }

    @Test
    void isNicknameDuplicated_닉네임_중복_확인() {
        // given
        when(userRepository.existsByNickname("testnick")).thenReturn(true);

        // when
        boolean result = userProfileService.isNicknameDuplicated("testnick");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void getMyProfile_성공() {
        // given
        user.setNickname("usernick");
        user.setEmail("user@example.com");
        user.setGender(Gender.MALE);
        user.setLatitude(37.5);
        user.setLongitude(127.0);
        user.setBirthdate(LocalDate.of(1990, 10, 10));
        user.setProfileImage("profile.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserProfileDto result = userProfileService.getMyProfile(1L);

        // then
        assertThat(result.getNickname()).isEqualTo("usernick");
        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }
}