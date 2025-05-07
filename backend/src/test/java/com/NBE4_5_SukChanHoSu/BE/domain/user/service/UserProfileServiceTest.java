package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.ProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileUpdateRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.global.util.S3Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Util s3Util;

    @InjectMocks
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("createProfile 테스트")
    class CreateProfileTest {
        void createProfile_success() throws IOException {
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
            when(s3Util.uploadFile(any(MultipartFile.class))).thenReturn("profile.jpg"); // S3Util 관련 mock 추가
            ProfileRequest dto = ProfileRequest.builder()
                    .nickname("testuser")
                    .gender(Gender.Male)
                    .latitude(37.5665)
                    .longitude(126.9780)
                    .birthdate(LocalDate.of(2000, 1, 1))
                    .profileImage("profile.jpg")
                    .build();

            userProfileService.createProfile(userId, dto,null);

            assertThat(userProfile.getNickName()).isEqualTo(dto.getNickname());
            verify(userProfileRepository, times(1)).save(any(UserProfile.class));
            verify(userRepository, times(1)).findById(userId);
            verify(userProfileRepository, times(1)).findByUserId(userId);
        }

        @Test // 이미지 업로드 테스트 추가
        void createProfile_withImage_success() throws IOException {
            Long userId = 1L;
            User user = User.builder().id(userId).email("test@example.com").build();
            UserProfile savedUserProfile = UserProfile.builder().userId(userId).nickName("testuser").gender(Gender.Male).profileImage("s3-profile.jpg").latitude(37.5665).longitude(126.9780).birthdate(LocalDate.of(2000, 1, 1)).user(user).build(); // 처음부터 S3 URL로 설정
            MockMultipartFile imageFile = new MockMultipartFile("profileImage", "image.jpg", "image/jpeg", "some image".getBytes());
            ProfileRequest dto = ProfileRequest.builder().nickname("testuser").gender(Gender.Male).latitude(37.5665).longitude(126.9780).birthdate(LocalDate.of(2000, 1, 1)).profileImage("profile.jpg").build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
            when(userProfileRepository.save(any(UserProfile.class))).thenReturn(savedUserProfile); // 저장 시 S3 URL을 가진 객체 반환
            when(s3Util.uploadFile(any(MultipartFile.class))).thenReturn("s3-profile.jpg"); // S3 URL 반환 가정

            ProfileResponse response = userProfileService.createProfile(userId, dto, imageFile); // 반환되는 ProfileResponse 캡처

            assertThat(response.getProfileImage()).isEqualTo("s3-profile.jpg"); // ProfileResponse의 이미지 URL 검증
            verify(userProfileRepository, times(1)).save(any(UserProfile.class));
            verify(userRepository, times(1)).findById(userId);
            verify(userProfileRepository, times(1)).findByUserId(userId);
            verify(s3Util, times(1)).uploadFile(any(MultipartFile.class));
        }


        @Test
        @DisplayName("이미 프로필이 등록된 경우 예외를 던진다.")
        void createProfile_alreadyExists() throws IOException {
            // given
            Long userId = 1L;
            UserProfile existingUserProfile = new UserProfile();
            existingUserProfile.setNickName("alreadySet");
            ProfileRequest dto = ProfileRequest.builder()
                    .nickname("testuser")
                    .latitude(37.5)
                    .longitude(127.0)
                    .gender(Gender.Male)
                    .profileImage("test.jpg")
                    .birthdate(LocalDate.now())
                    .build();

            // userRepository.findById에 대한 Mocking 설정 추가
            when(userRepository.findById(userId)).thenReturn(Optional.of(new User())); // 존재하는 User 반환

            when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existingUserProfile)); // 이미 존재하는 프로필 반환

            // when & then
            assertThatThrownBy(() -> userProfileService.createProfile(userId, dto,null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 프로필이 등록된 사용자입니다.");

            // save 메서드가 호출되지 않았는지 확인 (테스트의 의도에 따라 추가)
            verify(userProfileRepository, never()).save(any(UserProfile.class));
            verify(userRepository, times(1)).findById(userId);
            verify(userProfileRepository, times(1)).findByUserId(userId);
            verify(s3Util, never()).uploadFile(any(MultipartFile.class)); // 이미지 업로드 안했으므로 호출 안됨
        }

        @Nested
        @DisplayName("updateProfile 테스트")
        class UpdateProfileTest {

            @Test
            @DisplayName("정상적으로 프로필을 수정한다.")
            void updateProfile_success() throws IOException {
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
                doNothing().when(s3Util).deleteFile(anyString());// S3Util 관련 mock 추가 (삭제될 수 있음)
                when(s3Util.uploadFile(any(MultipartFile.class))).thenReturn("new.jpg"); // S3Util 관련 mock 추가
                // when
                ProfileResponse responseDto = userProfileService.updateProfile(userId, dto,null);

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

        @Nested
        @DisplayName("getMyProfile 테스트")
        class GetMyProfileTest {
            @Test
            @DisplayName("내 프로필을 정상 조회한다.")
            void getMyProfile_success() {
                // given (주어진 상황)

                Long userId = 1L;
                User user = User.builder()
                        .id(userId)
                        .email("test@example.com") // 이메일 추가
                        .build();
                UserProfile userProfile = UserProfile.builder()
                        .userId(userId)
                        .nickName("닉네임")
                        .gender(null)
                        .profileImage("profile.jpg")
                        .latitude(37.5665)
                        .longitude(126.9780)
                        .introduce("소개입니다.")
                        .birthdate(LocalDate.of(2000, 1, 1))
                        .user(user) // User 객체를 UserProfile에 연결합니다. 이 부분이 누락되어 NullPointerException이 발생했습니다.
                        .build();

                when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

                // when (테스트 실행)
                ProfileResponse result = userProfileService.getMyProfile(userId);

                // then (결과 확인)
                assertThat(result.getNickname()).isEqualTo("닉네임");
                assertThat(result.getIntroduce()).isEqualTo("소개입니다.");
                assertThat(result.getEmail()).isEqualTo("test@example.com"); // 이제 User 객체가 있으므로 이메일을 정상적으로 가져올 수 있습니다.
            }
        }
    }
}