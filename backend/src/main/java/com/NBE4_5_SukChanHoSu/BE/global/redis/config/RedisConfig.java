package com.NBE4_5_SukChanHoSu.BE.global.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    // 추상화와 직렬화 세팅
    // CacheManager를 사용하면 spring에서 캐싱할 때 로컬 캐시가 아닌 redis에 캐시한다고 함
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key: String으로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Value: JSON으로 직렬화
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(serializer);

        // HashKey: String으로 직렬화
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // HashValue: JSON으로 직렬화
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }
}