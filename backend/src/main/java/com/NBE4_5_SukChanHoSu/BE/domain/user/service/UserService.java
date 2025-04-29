package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.MatchingRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikesRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.MatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.UserMatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
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

    // 상대방이 나를 좋아하는지 검증
    public boolean isAlreadyLiked(UserProfile fromUser, UserProfile toUser) {
        return userLikesRepository.existsByFromUserAndToUser(toUser,fromUser);
    }

    // 이미 좋아요한 상황인지 검증
    public boolean isAlreadyLikes(UserProfile fromUser, UserProfile toUser) {
        return userLikesRepository.existsByFromUserAndToUser(fromUser,toUser);
    }

    // like -> 매칭
    @Transactional
    public MatchingResponse matching(UserProfile fromUser, UserProfile toUser) {
        Matching matching = new Matching();

        // fromUser가 남자인 경우
        if(isMale(fromUser)){
            matching.setMaleUser(fromUser);
            matching.setFemaleUser(toUser);
        }
        // fromUser가 여자인 경우(toUser가 남자인 경우)
        else {
            matching.setMaleUser(toUser);
            matching.setFemaleUser(fromUser);
        }

        matching.setMatchingTime(LocalDateTime.now());
        matchingRepository.save(matching);

        // 좋아요 관계 삭제
        userLikesRepository.deleteByFromUserAndToUser(toUser,fromUser);
        userLikesRepository.deleteByFromUserAndToUser(fromUser,toUser);
        // 응답 생성
        MatchingResponse matchingResponse = new MatchingResponse();
        matchingResponse.setMatching(matching);
        matchingResponse.setMaleUser(matching.getMaleUser());
        matchingResponse.setFemaleUser(matching.getFemaleUser());
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

        List<UserMatchingResponse> responses = new ArrayList<>();
        // 남자 유저
        if(isMale(user)){
            List<Matching> matches = matchingRepository.findByMaleUser(user);
            for(Matching matching: matches) {
                // 매칭된 여자 유저 리스트에 등록
                responses.add(new UserMatchingResponse(matching.getFemaleUser(),matching));
            }
        }
        // 여자 유저
        else {
            List<Matching> matches = matchingRepository.findByFemaleUser(user);
            for(Matching matching: matches) {
                // 매칭된 남자 유저 리스트에 등록
                responses.add(new UserMatchingResponse(matching.getMaleUser(),matching));
            }
        }
        return responses;
    }

    // 남자인지 검증 (남자:true,여자:false)
    public boolean isMale(UserProfile user) {
        return user.getGender().equals(Gender.Male);
    }

    // 매칭테이블에 이미 있는지 검증
    public boolean isAlreadyMatched(UserProfile fromUser, UserProfile toUser) {
        if(isMale(fromUser)){
            return matchingRepository.existsByMaleUserOrFemaleUser(fromUser,toUser);
        }else{
            return matchingRepository.existsByMaleUserOrFemaleUser(toUser,fromUser);
        }
    }

}
