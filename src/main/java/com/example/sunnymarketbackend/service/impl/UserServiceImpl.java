package com.example.sunnymarketbackend.service.impl;

import com.example.sunnymarketbackend.dao.RoleDao;
import com.example.sunnymarketbackend.dto.UserUpadteRequest;
import com.example.sunnymarketbackend.entity.LoginRecord;
import com.example.sunnymarketbackend.entity.Role;
import com.example.sunnymarketbackend.security.JwtUtil;
import com.example.sunnymarketbackend.service.UserService;

import com.example.sunnymarketbackend.dao.UserDao;
import com.example.sunnymarketbackend.dto.UserLoginRequest;
import com.example.sunnymarketbackend.dto.UserRegisterRequest;
import com.example.sunnymarketbackend.entity.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<Role> getRoleByUserId(Long userId) {
        List<Role> role = userDao.getRoleByUserId(userId);
        return role;
    }

    @Transactional
    @Override
    public Long register(UserRegisterRequest userRegisterRequest) {
        // 檢查註冊 email
        Users user = userDao.getUserByEmail(userRegisterRequest.getEmail());
        if (user != null) {
            // 使用 {} 表示變數
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

        // 創建帳號
        userDao.createUser(newUser);

        Role normalRole = roleDao.getRoleByName("ROLE_USER");
        userDao.addRoleForUserId(newUser.getUserId(), normalRole.getRoleId());

        return newUser.getUserId();
    }

    @Override
    public Users getUserById(Long userId) {
        Users user = userDao.getUserById(userId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    @Override
    public Users login(UserLoginRequest userLoginRequest) {
        Users user = userDao.getUserByEmail(userLoginRequest.getEmail());

        // 檢查 User 是否存在
        if (user == null) {
            log.warn("該 email {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 強制停止請求
        }

        // 比較明文密碼與資料庫加密密碼 密碼正確 return user
        if (passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) { // 如果前端傳來的值與資料庫一致
            return user;
        } else {
            log.warn("email {} 的密碼不正確", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 強制停止請求
        }
    }

    @Override
    public void loginRecord(Long userId, HttpServletRequest request) {

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr(); // 獲取原始請求 IP
        }

        Parser uaParser = new Parser();
        Client client = uaParser.parse(request.getHeader("User-Agent"));

        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setLoginTime(LocalDateTime.now());
        loginRecord.setIpAddress(ipAddress);
        loginRecord.setUserId(userId);
        loginRecord.setOsName(client.os.family);
        loginRecord.setBrowser(client.userAgent.family);

        userDao.addLoginRecordToUserId(loginRecord);
    }

    public List<LoginRecord> getLoginRecordByUserId(Long userId) {
        return userDao.getLoginRecordByUserId(userId);
    }

    @Override
    public void updateUser(UserUpadteRequest userUpadteRequest) {

        Users users = new Users();
        users.setUserId(userUpadteRequest.getUserId());
        users.setUsername(userUpadteRequest.getUsername());
        users.setAddress(userUpadteRequest.getAddress());
        users.setPhoneNumber(userUpadteRequest.getPhoneNumber());
        users.setBirthday(userUpadteRequest.getBirthday());
        users.setLastModifiedDate(LocalDateTime.now());

        userDao.updateUser(users);
    }
}
