package com.NBE4_5_SukChanHoSu.BE.domain.User.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileUpdateRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("createProfile 테스트")
    class CreateProfileTest {
        @Test
        @DisplayName("정상적으로 프로필을 생성한다.")
        void createProfile_success() {
            // given
            Long userId = 1L;
            UserProfile userProfile = new UserProfile();
            ProfileRequestDto dto = ProfileRequestDto.builder()
                    .nickname("testuser")
                    .gender(null)
                    .latitude(37.5665)
                    .longitude(126.9780)
                    .birthdate(LocalDate.of(2000, 1, 1))
                    .profileImage("profile.jpg")
                    .build();

            when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

            // when
            userProfileService.createProfile(userId, dto);

            // then
            assertThat(userProfile.getNickName()).isEqualTo(dto.getNickname());
            assertThat(userProfile.getProfileImage()).isEqualTo(dto.getProfileImage());
            verify(userProfileRepository, times(1)).save(userProfile);
        }

        @Test
        @DisplayName("이미 프로필이 등록된 경우 예외를 던진다.")
        void createProfile_alreadyExists() {
            // given
            Long userId = 1L;
            UserProfile userProfile = new UserProfile();
            userProfile.setNickName("alreadySet");
            ProfileRequestDto dto = ProfileRequestDto.builder()
                    .nickname("testuser")
                    .build();

            when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

            // when & then
            assertThatThrownBy(() -> userProfileService.createProfile(userId, dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 프로필이 등록된 사용자입니다.");
        }
    }

    @Nested
    @DisplayName("updateProfile 테스트")
    class UpdateProfileTest {
        @Test
        @DisplayName("정상적으로 프로필을 수정한다.")
        void updateProfile_success() {
            // given
            Long userId = 1L;
            UserProfile userProfile = new UserProfile();
            ProfileUpdateRequestDto dto = ProfileUpdateRequestDto.builder()
                    .nickname("newnickname")
                    .introduce("새로운 소개")
                    .build();

            when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

            // when
            userProfileService.updateProfile(userId, dto);

            // then
            assertThat(userProfile.getNickName()).isEqualTo(dto.getNickname());
            assertThat(userProfile.getIntroduce()).isEqualTo(dto.getIntroduce());
            verify(userProfileRepository, times(1)).save(userProfile);
        }
    }

    @Nested
    @DisplayName("isNicknameDuplicated 테스트")
    class NicknameDuplicateTest {
        @Test
        @DisplayName("닉네임이 중복인 경우 true를 반환한다.")
        void nicknameDuplicated_true() {
            // given
            String nickname = "duplicateNickname";
            when(userProfileRepository.existsByNickName(nickname)).thenReturn(true);

            // when
            boolean result = userProfileService.isNicknameDuplicated(nickname);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("닉네임이 중복이 아닌 경우 false를 반환한다.")
        void nicknameDuplicated_false() {
            // given
            String nickname = "uniqueNickname";
            when(userProfileRepository.existsByNickName(nickname)).thenReturn(false);

            // when
            boolean result = userProfileService.isNicknameDuplicated(nickname);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getMyProfile 테스트")
    class GetMyProfileTest {
        @Test
        @DisplayName("내 프로필을 정상 조회한다.")
        void getMyProfile_success() {
            // given
            Long userId = 1L;
            UserProfile userProfile = new UserProfile();
            userProfile.setNickName("nickname");
            userProfile.setGender(null);
            userProfile.setProfileImage("profile.jpg");
            userProfile.setLatitude(37.5665);
            userProfile.setLongitude(126.9780);
            userProfile.setBirthdate(LocalDate.of(2000, 1, 1));
            userProfile.setIntroduce("소개입니다.");

            when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

            // when
            ProfileResponseDto result = userProfileService.getMyProfile(userId);

            // then
            assertThat(result.getNickname()).isEqualTo("nickname");
            assertThat(result.getIntroduce()).isEqualTo("소개입니다.");
        }
    }
}
