package com.NBE4_5_SukChanHoSu.BE.global.exception.security;


import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;

public abstract class SecurityException extends ServiceException {
	public SecurityException(String code, String message) {
		super(code, message);
	}
}
