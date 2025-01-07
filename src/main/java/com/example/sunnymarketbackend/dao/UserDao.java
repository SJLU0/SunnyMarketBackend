package com.example.sunnymarketbackend.dao;

import com.example.sunnymarketbackend.entity.LoginRecord;
import com.example.sunnymarketbackend.entity.Role;
import com.example.sunnymarketbackend.entity.Users;

import java.util.List;

public interface UserDao {

    Users getUserById(Long userId);

    Users getUserByEmail(String email);

    Long createUser(Users users);

    // 權限相關
    List<Role> getRoleByUserId(Long userId);

    //新增註冊帳號的預設權限
    void addRoleForUserId(Long userId, Long roleId);

    void addLoginRecordToUserId(LoginRecord loginRecord);

    List<LoginRecord> getLoginRecordByUserId(Long userId);

    void updateUser(Users users);
}