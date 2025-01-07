package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.entity.Users;

import java.util.Map;

public interface GoogleLoginService {

    Map<String,Object> buildAuthUrl();

    Users googleLogin(String code);

}
