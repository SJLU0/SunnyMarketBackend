package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.dto.ErrorMessage;
import com.example.sunnymarketbackend.dto.GoogleLoginRequest;
import com.example.sunnymarketbackend.dto.GoogleUserData;
import com.example.sunnymarketbackend.service.GoogleLoginService;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/google")
public class GoogleLoginController {

    @Autowired
    private GoogleLoginService googleLoginService;

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
    public ResponseEntity<GoogleUserData> getCode(@RequestBody GoogleLoginRequest googleLoginRequest) {
        GoogleUserData googleUserData = googleLoginService.getUserInfo(googleLoginRequest.getCode());
        return ResponseEntity.ok().body(googleUserData);
    }

    //TODO 將 refreshToken 存到資料庫，並將邏輯改換至 Service
    @PostMapping("/refreshToken")
    public String refreshToken(@RequestParam String refreshToken) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // 使用 Google 所提供的 token url，傳遞 refresh_token 的值過去，即可取得到新的 access token
        String tokenUrl = GOOGLE_TOKEN_URL;

        // 填寫 request body 中的請求參數
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        // 發送請求
        String result;
        try {
            result = restTemplate.postForObject(
                    tokenUrl,
                    new HttpEntity<>(body, headers),
                    String.class
            );
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }
}
