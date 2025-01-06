package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.dto.GoogleLoginRequest;
import com.example.sunnymarketbackend.dto.GoogleUserDataResponse;
import com.example.sunnymarketbackend.security.JwtUtil;
import com.example.sunnymarketbackend.service.GoogleLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;



import java.util.Map;

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
        return ResponseEntity.ok().body(urlMap);
    }

    @PostMapping("/getGoogleCode")
    public ResponseEntity<GoogleUserDataResponse> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        GoogleUserDataResponse googleUserData = googleLoginService.googleLogin(googleLoginRequest.getCode());
        String token = jwtUtil.generateToken(googleUserData.getProviderId(), googleUserData.getEmail());
        googleUserData.setToken(token);
        return ResponseEntity.ok().body(googleUserData);
    }
}
