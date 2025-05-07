package com.NBE4_5_SukChanHoSu.BE.global.exception.redis;

import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;

public class RedisSerializationException extends ServiceException {
    public RedisSerializationException(String code, String message) {
        super(code, message);
    }
}
