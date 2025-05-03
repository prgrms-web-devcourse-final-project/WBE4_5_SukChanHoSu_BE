package com.NBE4_5_SukChanHoSu.BE.global.interceptor;

import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ProfileCheckInterceptor implements HandlerInterceptor {

    private final UserProfileService userProfileService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!userProfileService.existsProfileByUserId(userId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("프로필을 먼저 등록해야 합니다.");
            return false;
        }

        return true;
    }
}
