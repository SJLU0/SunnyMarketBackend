package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.dto.GoogleUserDataResponse;

import java.util.Map;

public interface GoogleLoginService {

    Map<String,Object> buildAuthUrl();

    GoogleUserDataResponse googleLogin(String code);

}
