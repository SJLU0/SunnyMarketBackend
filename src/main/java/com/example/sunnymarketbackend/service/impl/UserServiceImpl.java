package com.example.sunnymarketbackend.service.impl;

import com.example.sunnymarketbackend.dao.RoleDao;
import com.example.sunnymarketbackend.entity.Role;
import com.example.sunnymarketbackend.service.UserService;

import com.example.sunnymarketbackend.dao.UserDao;
import com.example.sunnymarketbackend.dto.UserLoginRequest;
import com.example.sunnymarketbackend.dto.UserRegisterRequest;
import com.example.sunnymarketbackend.entity.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Long register(UserRegisterRequest userRegisterRequest) {
        //檢查註冊 email
        Users user = userDao.getUserByEmail(userRegisterRequest.getEmail());
        if(user != null){
            //使用 {} 表示變數
            log.warn("該 email {} 已被註冊", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String hashedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
        userRegisterRequest.setPassword(hashedPassword);

        Users newUser = new Users();
        newUser.setEmail(userRegisterRequest.getEmail());
        newUser.setPassword(hashedPassword);
        newUser.setAddress(userRegisterRequest.getAddress());
        newUser.setUsername(userRegisterRequest.getUsername());
        newUser.setBirthday(userRegisterRequest.getBirthday());
        newUser.setPhoneNumber(userRegisterRequest.getPhoneNumber());
        newUser.setCreatedDate(LocalDateTime.now());
        newUser.setLastModifiedDate(LocalDateTime.now());

        //創建帳號
        userDao.createUser(newUser);

        Role normalRole = roleDao.getRoleByName("ROLE_USER");
        userDao.addRoleForUserId(newUser.getUserId(), normalRole.getRoleId());

        return newUser.getUserId();
    }

    @Override
    public Users getUserById(Long userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Users login(UserLoginRequest userLoginRequest) {
        Users user = userDao.getUserByEmail(userLoginRequest.getEmail());

        //檢查 User 是否存在
        if(user == null){
            log.warn("該 email {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); //強制停止請求
        }


        //比較明文密碼與資料庫加密密碼 密碼正確 return user
        if(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())){ //如果前端傳來的值與資料庫一致
            return user;
        }else{
            log.warn("email {} 的密碼不正確", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); //強制停止請求
        }
    }
}
