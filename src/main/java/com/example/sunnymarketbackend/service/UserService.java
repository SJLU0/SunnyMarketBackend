package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.dto.UserLoginRequest;
import com.example.sunnymarketbackend.dto.UserRegisterRequest;
import com.example.sunnymarketbackend.entity.Users;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface UserService {

    Users getUserById(Long userId);

    Long register(UserRegisterRequest userRegisterRequest);

    Users login(UserLoginRequest userLoginRequest);

    void loginRecord(Long userId ,HttpServletRequest request);
}
