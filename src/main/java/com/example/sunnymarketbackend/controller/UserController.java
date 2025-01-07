package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.dto.ErrorMessage;
import com.example.sunnymarketbackend.dto.UserLoginRequest;
import com.example.sunnymarketbackend.dto.UserRegisterRequest;
import com.example.sunnymarketbackend.dto.UserUpadteRequest;
import com.example.sunnymarketbackend.entity.LoginRecord;
import com.example.sunnymarketbackend.entity.Users;
import com.example.sunnymarketbackend.security.JwtUtil;
import com.example.sunnymarketbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/loginRecord/{userId}")
    public ResponseEntity<List<LoginRecord>> loginRecord(@PathVariable Long userId) {
        List<LoginRecord> loginRecordList = userService.getLoginRecordByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(loginRecordList);
    }

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        Long userId = userService.register(userRegisterRequest);

        Users user = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest userLoginRequest, HttpServletRequest request) {
        Users user = userService.login(userLoginRequest);

        if(user != null) {
            userService.loginRecord(user.getUserId() ,request);
            Map token = jwtUtil.generateToken(user.getUserId(), user.getEmail());
            token.put("message", "登入成功，請稍後 login wating...");
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }else{
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setMessage("登入失敗，帳號或密碼錯誤");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }
    }

    //TODO 修改使用者資料
    @PostMapping("/updateUesr/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                       @RequestBody UserUpadteRequest userUpadteRequest){
        userUpadteRequest.setUserId(userId);
        userService.updateUser(userUpadteRequest);
        return ResponseEntity.status(HttpStatus.OK).body("更新成功");
    }
}
