package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.dto.GoogleLoginRequest;
import com.example.sunnymarketbackend.dto.GoogleUserDataResponse;
import com.example.sunnymarketbackend.entity.Users;
import com.example.sunnymarketbackend.security.JwtUtil;
import com.example.sunnymarketbackend.service.GoogleLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;



import java.util.Map;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@RestController
@RequestMapping("/google")
public class GoogleLoginController {

    @Autowired
    private GoogleLoginService googleLoginService;

    @Autowired
    private JwtUtil jwtUtil;

    private String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    private String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @GetMapping("/buildAuthUrl")
    public ResponseEntity<Map<String, Object>> buildAuthUrl() {
        Map<String, Object> urlMap = googleLoginService.buildAuthUrl();
        return ResponseEntity.status(HttpStatus.OK).body(urlMap);
    }

    @PostMapping("/getGoogleCode")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        Users user = googleLoginService.googleLogin(googleLoginRequest.getCode());
        Map<String, Object> token = jwtUtil.generateToken(user.getUserId(), user.getEmail());
        token.put("message", "登入成功");
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
