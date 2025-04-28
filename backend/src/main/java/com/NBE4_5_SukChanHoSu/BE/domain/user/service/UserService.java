package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.MatchingRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikesRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.MatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.UserMatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final UserLikesRepository userLikesRepository;
    private final MatchingRepository matchingRepository;

    public UserProfile findUser(Long userId) {
        UserProfile userProfile =  userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("401","존재하지 않는 유저입니다."));
        return userProfile;
    }

    @Transactional
    public void likeUser(UserProfile fromUser, UserProfile toUser) {
        // 좋아요 관계 생성
        UserLikes like = new UserLikes();
        like.setFromUser(fromUser);
        like.setToUser(toUser);
        like.setLikeTime(LocalDateTime.now());
        userLikesRepository.save(like);
    }

    public boolean isMatched(UserProfile fromUser, UserProfile toUser) {
        return userLikesRepository.existsByFromUserAndToUser(toUser,fromUser);
    }

    // like -> 매칭
    @Transactional
    public MatchingResponse matching(UserProfile fromUser, UserProfile toUser) {
        Matching matching = new Matching();
        matching.setUser1(fromUser);
        matching.setUser2(toUser);
        matching.setMatchingTime(LocalDateTime.now());
        matchingRepository.save(matching);

        // 좋아요 관계 삭제
        userLikesRepository.deleteByFromUserAndToUser(toUser,fromUser);
        userLikesRepository.deleteByFromUserAndToUser(fromUser,toUser);
        // 응답 생성
        MatchingResponse matchingResponse = new MatchingResponse();
        matchingResponse.setUser1Nickname(fromUser.getNickName());
        matchingResponse.setUser2Nickname(toUser.getNickName());
        matchingResponse.setMatchingTime(LocalDateTime.now());

        return matchingResponse;
    }

    // like 목록 조회
    public List<UserProfile> getUserLikes(UserProfile user) {
        List<UserProfile> likesUsers = new ArrayList<>();

        for(UserLikes like: user.getLikes()) {  // 내가 좋아요한 사용자 목록
            UserProfile likedUser = like.getToUser();   // 좋아요를 받은 사용자
            if(likedUser != null) {
                // 내가 좋아요 한 사용자 리스트에 추가
                likesUsers.add(likedUser);
            }
        }
        return likesUsers;
    }

    // user의 liked 목록 조회
    public List<UserProfile> getUserLiked(UserProfile user) {
        List<UserProfile> likedUsers = new ArrayList<>();

        for(UserLikes like: user.getLikedBy()) {    // 나를 좋아요한 사용자 목록
            UserProfile likesUser = like.getFromUser(); // 좋아요를 보낸 사용자
            if(likesUser != null) {
                // 나를 좋아요한 사용자 리스트에 추가
                likedUsers.add(likesUser);
            }
        }
        return likedUsers;
    }

    // match 목록 조회
    public List<UserMatchingResponse> getUserMatches(UserProfile user) {
        List<Matching> matches = matchingRepository.findByUser1OrUser2(user,user);
        List<UserMatchingResponse> userMatchingRespons = new ArrayList<>();

        for(Matching match : matches) {
            UserMatchingResponse response = new UserMatchingResponse();
            if(match.getUser1().equals(user)) {
                response.setUserId(match.getUser2().getUserId());
                response.setUserNickname(match.getUser2().getNickName());
                response.setMatchingTime(match.getMatchingTime());
            }else {
                response.setUserId(match.getUser1().getUserId());
                response.setUserNickname(match.getUser1().getNickName());
                response.setMatchingTime(match.getMatchingTime());
            }
            userMatchingRespons.add(response);
        }
        return userMatchingRespons;
    }
}
