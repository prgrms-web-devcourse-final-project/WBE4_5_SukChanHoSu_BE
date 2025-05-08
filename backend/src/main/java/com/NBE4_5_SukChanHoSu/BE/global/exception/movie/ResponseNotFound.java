package com.NBE4_5_SukChanHoSu.BE.global.exception.movie;

import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;

public class ResponseNotFound extends ServiceException {
    public ResponseNotFound(String code, String message) {
        super(code, message);
    }
}
