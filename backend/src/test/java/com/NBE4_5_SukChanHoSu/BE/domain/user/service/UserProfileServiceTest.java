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

            UserProfile userProfile = UserProfile.builder()
                    .userId(userId)
                    .nickName(null)
                    .gender(Gender.Male)
                    .profileImage("default.jpg")
                    .latitude(0.0)
                    .longitude(0.0)
                    .build();

//            userProfile.setUserId(userId);
//            userProfile.setNickName(null);
//            userProfile.setGender(Gender.Male);
//            userProfile.setProfileImage("default.jpg");
//            userProfile.setLatitude(0.0);
//            userProfile.setLongitude(0.0);

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

            // User 객체 생성 및 Mocking
            User user = User.builder()
                    .id(userId)
                    .email("test@example.com")
                    .name("Test User")
                    .build();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserProfile userProfile = new UserProfile();
            userProfile.setNickName("alreadySet");
            ProfileRequest dto = ProfileRequest.builder()
                    .nickname("testuser")
                    .build();

            when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

            // when & then
            assertThatThrownBy(() -> userProfileService.createProfile(userId, dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 프로필이 등록된 사용자입니다.");

            // save 메서드가 호출되지 않았는지 확인 (테스트의 의도에 따라 추가)
            verify(userProfileRepository, never()).save(any(UserProfile.class));
        }

        @Nested
        @DisplayName("updateProfile 테스트")
        class UpdateProfileTest {

            @Test
            @DisplayName("정상적으로 프로필을 수정한다.")
            void updateProfile_success() {
                // given
                Long userId = 1L;

                UserProfile existingUserProfile = UserProfile.builder()
                        .userId(userId)
                        .nickName("oldNickname")
                        .introduce("oldIntroduce")
                        .gender(Gender.Male)
                        .profileImage("old.jpg")
                        .latitude(37.0)
                        .longitude(127.0)
                        .birthdate(LocalDate.of(1990, 1, 1))
                        .user(User.builder().id(userId).email("test@example.com").build())
                        .build();

                ProfileUpdateRequest dto = ProfileUpdateRequest.builder()
                        .nickname("newnickname")
                        .introduce("새로운 소개")
                        .profileImage("new.jpg")
                        .latitude(38.0)
                        .longitude(128.0)
                        .build();

                UserProfile updatedUserProfile = UserProfile.builder()
                        .userId(userId)
                        .nickName("newnickname") // 업데이트된 닉네임
                        .introduce("새로운 소개")
                        .gender(Gender.Male) // 기존 값 유지 (DTO에 없음)
                        .profileImage("new.jpg")
                        .latitude(38.0)
                        .longitude(128.0)
                        .birthdate(LocalDate.of(1990, 1, 1))
                        .user(User.builder().id(userId).email("test@example.com").build())
                        .build();

                // mock 동작 정의
                when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existingUserProfile));
                when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updatedUserProfile); // 업데이트된 객체 반환

                // when
                ProfileResponse responseDto = userProfileService.updateProfile(userId, dto);

                // then
                assertThat(responseDto.getNickname()).isEqualTo(dto.getNickname());
                assertThat(responseDto.getIntroduce()).isEqualTo(dto.getIntroduce());
                assertThat(responseDto.getProfileImage()).isEqualTo(dto.getProfileImage());
                assertThat(responseDto.getLatitude()).isEqualTo(dto.getLatitude());
                assertThat(responseDto.getLongitude()).isEqualTo(dto.getLongitude());

                verify(userProfileRepository, times(1)).findByUserId(userId);
                verify(userProfileRepository, times(1)).save(any(UserProfile.class));
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

        @Test
        @DisplayName("내 프로필을 정상 조회한다.")
        void getMyProfile_success() {
            // given
            Long userId = 1L;

            // User 객체 생성
            User user = User.builder()
                    .id(userId)
                    .email("test@example.com")
                    .name("Test User")
                    .build();

            // UserProfile 객체 생성 시 user 필드 설정
            UserProfile userProfile = UserProfile.builder()
                    .userId(userId)
                    .nickName("nickname")
                    .gender(null)
                    .profileImage("profile.jpg")
                    .latitude(37.5665)
                    .longitude(126.9780)
                    .introduce("소개입니다.")
                    .birthdate(LocalDate.of(2000, 1, 1))
                    .user(user) // ★ user 필드 설정
                    .build();

            when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

            // when
            ProfileResponse result = userProfileService.getMyProfile(userId);

            // then
            assertThat(result.getNickname()).isEqualTo("nickname");
            assertThat(result.getIntroduce()).isEqualTo("소개입니다.");
            assertThat(result.getEmail()).isEqualTo("test@example.com"); // User 객체의 email 검증 추가
        }
    }
}