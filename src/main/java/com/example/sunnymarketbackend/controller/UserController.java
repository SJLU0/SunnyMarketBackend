package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.dto.ErrorMessage;
import com.example.sunnymarketbackend.dto.UserLoginRequest;
import com.example.sunnymarketbackend.dto.UserLoginResponse;
import com.example.sunnymarketbackend.dto.UserRegisterRequest;
import com.example.sunnymarketbackend.entity.Role;
import com.example.sunnymarketbackend.entity.Users;
import com.example.sunnymarketbackend.security.JwtUtil;
import com.example.sunnymarketbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        Long userId = userService.register(userRegisterRequest);

        Users user = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        Users user = userService.login(userLoginRequest);

        if(user != null) {
            Map<String, Object> extraClaims = new HashMap();
            String token = jwtUtil.generateToken(extraClaims, user.getEmail(), user.getUserId());
            List<Role> role = userService.getRoleByUserId(user.getUserId());
            UserLoginResponse userLoginResponse = new UserLoginResponse();
            userLoginResponse.setToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(userLoginResponse);
        }
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage("登入失敗，帳號或密碼錯誤");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }
}
