package com.NBE4_5_SukChanHoSu.BE.global.exception.movie;

import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;

public class ParsingException extends ServiceException {
    public ParsingException(String code, String message) {
        super(code, message);
    }
}