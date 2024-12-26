package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.dto.UserLoginRequest;
import com.example.sunnymarketbackend.dto.UserRegisterRequest;
import com.example.sunnymarketbackend.entity.User;

public interface UserService {

    User getUserById(Long userId);

    Long register(UserRegisterRequest userRegisterRequest);

    User login(UserLoginRequest userLoginRequest);
}
