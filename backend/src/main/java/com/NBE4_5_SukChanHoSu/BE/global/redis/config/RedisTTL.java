package com.NBE4_5_SukChanHoSu.BE.global.redis.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RedisTTL {
    @Value("${spring.data.redis.time_to_live.likes}")
    private long likes;
    @Value("${spring.data.redis.time_to_live.liked}")
    private long liked;
    @Value("${spring.data.redis.time_to_live.matching}")
    private long matching;
    @Value("${spring.data.redis.time_to_live.data}")
    private long data;
    @Value("${spring.data.redis.time_to_live.rank}")
    private long rank;
    @Value("${spring.data.redis.time_to_live.detail}")
    private long detail;
}