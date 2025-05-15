package com.NBE4_5_SukChanHoSu.BE.global.security;

public interface OAuth2UserInfo {
    String getProviderId();

    String getProvider();

    String getProviderEmail();

    String getProviderName();
}
