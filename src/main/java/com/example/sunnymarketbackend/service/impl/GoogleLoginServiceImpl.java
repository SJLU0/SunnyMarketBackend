package com.example.sunnymarketbackend.service.impl;

import com.example.sunnymarketbackend.dao.RoleDao;
import com.example.sunnymarketbackend.dao.UserDao;
import com.example.sunnymarketbackend.dto.GoogleAccessTokenResponse;
import com.example.sunnymarketbackend.dto.GoogleUserDataResponse;
import com.example.sunnymarketbackend.entity.Role;
import com.example.sunnymarketbackend.entity.Users;
import com.example.sunnymarketbackend.service.GoogleLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleLoginServiceImpl implements GoogleLoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Value("${google.auth.url}")
    private String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    @Value("${google.token.url}")
    private String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Override
    public Map<String, Object> buildAuthUrl() {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(GOOGLE_AUTH_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("scope", "profile+email+openid")
                .queryParam("redirect_uri", "http://localhost:5173/user/login")
                .queryParam("state", generateRandomState())
                .queryParam("access_type", "offline");

        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("authUrl", uriBuilder.toUriString());

        return urlMap;
    }

    @Override
    public Users googleLogin(String code) {
        GoogleAccessTokenResponse googleAccessTokenResponse = getAccessToken(code);
        Users user = getGoogleUserByAccessToken(googleAccessTokenResponse);
        return user;
    }

    private GoogleAccessTokenResponse getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", "http://localhost:5173/user/login");

        // 發送請求
        String result;
        try {
            result = restTemplate.postForObject(
                    GOOGLE_TOKEN_URL,
                    new HttpEntity<>(body, headers),
                    String.class
            );
            // 使用 Jackson 將 JSON 字串轉換為物件
            ObjectMapper objectMapper = new ObjectMapper();
            GoogleAccessTokenResponse googleAccessTokenResponse = objectMapper.readValue(result, GoogleAccessTokenResponse.class);

            return googleAccessTokenResponse;
        } catch (Exception e) {
            throw new RuntimeException("無法從 Google 獲取 Access Token: " + e.getMessage(), e);
        }
    }

    @Transactional
    private Users getGoogleUserByAccessToken(GoogleAccessTokenResponse googleAccessTokenResponse) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + googleAccessTokenResponse.getAccessToken());
//        headers.setBearerAuth(accessToken);

        // call Google 的 api，取得使用者在 Google 中的基本資料
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        // 發送請求
        String result;
        try {
            result = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(headers),
                            String.class
                    )
                    .getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            GoogleUserDataResponse googleUserData = objectMapper.readValue(result, GoogleUserDataResponse.class);

            Users users = userDao.getUserByEmail(googleUserData.getEmail());

            if(users == null) {
                Users newUser = new Users();
                newUser.setEmail(googleUserData.getEmail());
                newUser.setProviderId(googleUserData.getProviderId());
                newUser.setUsername(googleUserData.getName());
                newUser.setProvider("google");
                newUser.setRefreshToken(googleAccessTokenResponse.getRefreshToken());
                newUser.setCreatedDate(LocalDateTime.now());
                newUser.setLastModifiedDate(LocalDateTime.now());
                userDao.createUser(newUser);

                Role normalRole = roleDao.getRoleByName("ROLE_USER");
                userDao.addRoleForUserId(newUser.getUserId(), normalRole.getRoleId());
            }

            return users;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private GoogleUserDataResponse getGoogleUserByRefeshToken(String refreshToken){
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

            ObjectMapper objectMapper = new ObjectMapper();
            GoogleUserDataResponse googleUserData = objectMapper.readValue(result, GoogleUserDataResponse.class);

            return googleUserData;
        } catch (Exception e) {
            result = e.toString();
            return null;
        }
    }

    private String generateRandomState() {
        SecureRandom sr = new SecureRandom();
        byte[] data = new byte[6];
        sr.nextBytes(data);
        return Base64.getUrlEncoder().encodeToString(data);
    }
}
