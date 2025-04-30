package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.ProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileUpdateRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
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

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("createProfile 테스트")
    class CreateProfileTest {
        void createProfile_success() {
            Long userId = 1L;

            User user = User.builder()
                    .id(userId)
                    .email("test@example.com")
                    .name("Test User")
                    .build();

            UserProfile userProfile = new UserProfile();
            userProfile.setUserId(userId);
            userProfile.setNickName(null);
            userProfile.setGender(Gender.Male);
            userProfile.setProfileImage("default.jpg");
            userProfile.setLatitude(0.0);
            userProfile.setLongitude(0.0);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));
            when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

            ProfileRequest dto = ProfileRequest.builder()
                    .nickname("testuser")
                    .gender(Gender.Male)
                    .latitude(37.5665)
                    .longitude(126.9780)
                    .birthdate(LocalDate.of(2000, 1, 1))
                    .profileImage("profile.jpg")
                    .build();

            userProfileService.createProfile(userId, dto);

            assertThat(userProfile.getNickName()).isEqualTo(dto.getNickname());
            verify(userProfileRepository, times(1)).save(any(UserProfile.class));
        }
        @Test
        @DisplayName("이미 프로필이 등록된 경우 예외를 던진다.")
        void createProfile_alreadyExists() {
            // given
            Long userId = 1L;
            UserProfile userProfile = new UserProfile();
            userProfile.setNickName("alreadySet");
            ProfileRequest dto = ProfileRequest.builder()
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
            userProfile.setUserId(userId); // ★ 꼭 필요!
            userProfile.setNickName("oldNickname");
            userProfile.setIntroduce("oldIntroduce");

            // 필수 필드 추가 설정 (서비스 로직에서 null이면 에러날 수 있음)
            userProfile.setGender(Gender.Male);
            userProfile.setProfileImage("old.jpg");
            userProfile.setLatitude(37.0);
            userProfile.setLongitude(127.0);
            userProfile.setBirthdate(LocalDate.of(1990, 1, 1));

            ProfileUpdateRequest dto = ProfileUpdateRequest.builder()
                    .nickname("newnickname")
                    .introduce("새로운 소개")
                    .gender(Gender.Female)
                    .profileImage("new.jpg")
                    .latitude(38.0)
                    .longitude(128.0)
                    .birthdate(LocalDate.of(2000, 1, 1))
                    .build();

            // mock 동작 정의
            when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));
            when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

            // when
            ProfileResponse responseDto = userProfileService.updateProfile(userId, dto);

            // then
            assertThat(responseDto.getNickname()).isEqualTo(dto.getNickname());
            assertThat(responseDto.getIntroduce()).isEqualTo(dto.getIntroduce());
            assertThat(responseDto.getGender()).isEqualTo(dto.getGender());
            assertThat(responseDto.getProfileImage()).isEqualTo(dto.getProfileImage());
            assertThat(responseDto.getLatitude()).isEqualTo(dto.getLatitude());
            assertThat(responseDto.getLongitude()).isEqualTo(dto.getLongitude());
            assertThat(responseDto.getBirthdate()).isEqualTo(dto.getBirthdate());

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
            ProfileResponse result = userProfileService.getMyProfile(userId);

            // then
            assertThat(result.getNickname()).isEqualTo("nickname");
            assertThat(result.getIntroduce()).isEqualTo("소개입니다.");
        }
    }
}