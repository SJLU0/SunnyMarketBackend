package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.dto.GoogleUserData;

import java.util.Map;

public interface GoogleLoginService {

    Map<String,Object> buildAuthUrl();

    GoogleUserData getUserInfo(String code);

}
