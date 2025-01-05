package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.dto.GoogleAccessTokenResponse;
import com.example.sunnymarketbackend.dto.GoogleUserData;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import java.util.Map;

public interface GoogleLoginService {

    Map<String,Object> buildAuthUrl();

    GoogleUserData getUserInfo(String code);

}
