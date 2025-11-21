package com.document.anhminh.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // <-- Thêm import
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // SỬA LẠI: Đọc khóa từ file properties
    private final Key secretKey;
    private final long jwtExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secretString,
            @Value("${jwt.expiration}") long jwtExpiration
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
    }

    // Tạo token từ username
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Dùng biến expiration
                .signWith(secretKey)
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