package com.NBE4_5_SukChanHoSu.BE.global.exception.security;

public class AuthenticationNotFoundException extends SecurityException {
	public AuthenticationNotFoundException(String code, String message) {
		super(code, message);
	}
}
