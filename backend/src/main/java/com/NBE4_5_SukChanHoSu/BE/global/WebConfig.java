package com.NBE4_5_SukChanHoSu.BE.global;

import com.NBE4_5_SukChanHoSu.BE.global.interceptor.ProfileCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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

    }@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // 프론트가 띄워지는 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 인증정보(Cookie 등) 포함 허용
    }
}
