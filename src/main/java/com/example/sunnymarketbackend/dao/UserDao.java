package com.example.sunnymarketbackend.dao;

import com.example.sunnymarketbackend.dto.UserRegisterRequest;
import com.example.sunnymarketbackend.entity.User;

public interface UserDao {

    User getUserById(Long userId);

    User getUserByEmail(String email);

    Long createUser(UserRegisterRequest userRegisterRequest);
}