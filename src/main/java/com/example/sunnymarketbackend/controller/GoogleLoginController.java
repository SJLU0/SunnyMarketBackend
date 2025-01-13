package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.dto.GoogleLoginRequest;
import com.example.sunnymarketbackend.dto.GoogleUserDataResponse;
import com.example.sunnymarketbackend.entity.Role;
import com.example.sunnymarketbackend.entity.Users;
import com.example.sunnymarketbackend.security.JwtUtil;
import com.example.sunnymarketbackend.service.GoogleLoginService;
import com.example.sunnymarketbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@RestController
@RequestMapping("/google")
public class GoogleLoginController {

    @Autowired
    private GoogleLoginService googleLoginService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/buildAuthUrl")
    public ResponseEntity<Map<String, Object>> buildAuthUrl() {
        Map<String, Object> urlMap = googleLoginService.buildAuthUrl();
        return ResponseEntity.status(HttpStatus.OK).body(urlMap);
    }

    @PostMapping("/getGoogleCode")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        Users user = googleLoginService.googleLogin(googleLoginRequest.getCode());
        List<Role> role = userService.getRoleByUserId(user.getUserId());
        Map<String, Object> token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), role.get(0).getRoleName());
        token.put("message", "登入成功");
        token.put("role", role.get(0).getRoleName());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
