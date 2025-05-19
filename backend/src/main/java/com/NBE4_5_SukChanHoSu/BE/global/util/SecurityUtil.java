package com.NBE4_5_SukChanHoSu.BE.global.util;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.responseCode.UserErrorCode;
import com.NBE4_5_SukChanHoSu.BE.global.exception.security.BadCredentialsException;
import com.NBE4_5_SukChanHoSu.BE.global.security.PrincipalDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    // 현재 인증된 사용자 아이디 반환
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof PrincipalDetails)) {
            throw new BadCredentialsException(
                    UserErrorCode.USER_UNAUTHORIZED.getCode(),
                    UserErrorCode.USER_UNAUTHORIZED.getMessage()
            );
        }

        return ((PrincipalDetails) authentication.getPrincipal()).getUser().getId();
    }

    // 현재 인증된 사용자 반환
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof PrincipalDetails)) {
            throw new BadCredentialsException(
                    UserErrorCode.USER_UNAUTHORIZED.getCode(),
                    UserErrorCode.USER_UNAUTHORIZED.getMessage()
            );
        }

        return ((PrincipalDetails) authentication.getPrincipal()).getUser();
    }
}
