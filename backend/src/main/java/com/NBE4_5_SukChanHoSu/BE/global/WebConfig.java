package com.NBE4_5_SukChanHoSu.BE.global;

import com.NBE4_5_SukChanHoSu.BE.global.interceptor.ProfileCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ProfileCheckInterceptor profileCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(profileCheckInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(

                        "/api/profile/check-nickname",
                        "/api/auth/**",              // 로그인/회원가입
                        "/swagger-ui/**", "/v3/api-docs/**"  // Swagger 등
                );
    }
}
