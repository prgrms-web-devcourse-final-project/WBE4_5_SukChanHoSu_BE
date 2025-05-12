package com.NBE4_5_SukChanHoSu.BE.global.security;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserStatus;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new UsernameNotFoundException("정지된 계정입니다.");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new UsernameNotFoundException("탈퇴된 계정입니다.");
        }
        return new PrincipalDetails(user);
    }
}