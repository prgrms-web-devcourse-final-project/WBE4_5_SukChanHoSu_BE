package com.NBE4_5_SukChanHoSu.BE.global.app;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
        FilterRegistrationBean<RequestContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestContextFilter());
        registrationBean.setOrder(Integer.MIN_VALUE);
        return registrationBean;
    }

    @Bean
    public Map<Long, Long> MovieIdToTid() {
        Map<Long, Long> movieIdToTid = new HashMap<>();

        String currentDir = System.getProperty("user.dir");
        System.out.println("📍 현재 실행 경로: " + currentDir);
        File csv = new File("backend/src/main/resources/data/ml-latest-small/links.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;
            boolean skipFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (skipFirstLine) {
                    skipFirstLine = false;
                    continue;
                }

                String[] token = line.split(",");

                if (token.length > 2) {
                    try {
                        Long movieId = Long.parseLong(token[0]);
                        Long tId = Long.parseLong(token[2]);

                        movieIdToTid.put(movieId, tId);
                    } catch (NumberFormatException e) {
                        System.err.println("숫자 파싱 오류: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("파일 읽기 오류: " + e.getMessage());
        }

        return movieIdToTid;
    }
}
