package com.document.anhminh.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Secret key để ký token
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); //akfjkasdjfasjdfkj

    // Tạo token từ username
    public String generateToken(String username) {
        // Thời gian sống của token (1h)
        // 1 giờ (ms)
        long jwtExpiration = 3600000;
        return Jwts.builder()
                .setSubject(username) // lưu vào subject(cridentials: tuyệt mật)
                .setIssuedAt(new Date(System.currentTimeMillis())) // thời gian phát hành
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // hết hạn
                .signWith(secretKey) // ký bằng secret key
                .compact();
    }

    // Lấy username từ token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Kiểm tra token hợp lệ không
    public boolean isTokenValid(String token, String username) {
        return (extractUsername(token).equals(username)) && !isTokenExpired(token);
    }

    // Kiểm tra token hết hạn chưa
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Lấy ngày hết hạn từ token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Lấy thông tin Claims từ token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}