package com.example.sunnymarketbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationTime}")
    private long expirationTime;

    // 提取 JWT 中的用戶名（主體），這裡假設主體是用戶的 email
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 通用方法，從 JWT 中提取指定的聲明（claims），如主體（subject）、過期時間等
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // 提取所有聲明
        return claimsResolver.apply(claims);  // 根據提供的 claimsResolver 函數提取具體聲明
    }


    public Map<String, Object> generateToken(Long userId, String email, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);
        extraClaims.put("role", role);
        String jwt = Jwts
                .builder()
                .setClaims(extraClaims)  // 設定額外聲明
                .setSubject(email)  // 設定主體為用戶的 email
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 設定當前時間為簽發時間
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // 使用從配置文件中加載的過期時間
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // 使用 HMAC SHA-256 演算法簽名，並提供密鑰
                .compact();  // 組裝 JWT
        Map<String, Object> token = new HashMap<>();
        token.put("token", jwt);
        return token;
    }

    // 檢查 JWT 是否有效（即用戶名是否匹配且 token 是否過期）
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);  // 提取 JWT 中的用戶名
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);  // 用戶名匹配且 token 未過期
    }

    // 檢查 JWT 是否過期
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // 如果過期時間早於當前時間，則 token 已過期
    }

    // 提取 JWT 中的過期時間
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // 提取過期時間聲明
    }

    // 解析並提取 JWT 的所有聲明
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())  // 使用密鑰來驗證 JWT 的簽名
                .build()
                .parseClaimsJws(token)  // 解析 JWT
                .getBody();  // 提取並返回聲明部分（不包含簽名）
    }

    // 獲取簽名所用的密鑰
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);  // 解碼密鑰
        return Keys.hmacShaKeyFor(keyBytes);  // 生成 HMAC SHA 密鑰
    }
}
