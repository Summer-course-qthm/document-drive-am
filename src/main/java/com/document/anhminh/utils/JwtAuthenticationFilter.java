package com.document.anhminh.utils;

import com.document.anhminh.service.CustomUserDetailsService;
import com.document.anhminh.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor; // <-- THÊM IMPORT NÀY
// BỎ import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
// BỎ import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor // <-- THÊM ANNOTATION NÀY
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // SỬA LẠI: Dùng "final" và bỏ "@Autowired"
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // Hàm hỗ trợ để lấy token từ Header HOẶC Cookie
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (!token.isEmpty() && isValidJwtFormat(token)) {
                return token;
            }
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // Tên cookie này ("jwt-token") PHẢI KHỚP với ViewLoginController
                if ("jwt-token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (token != null && !token.trim().isEmpty() && isValidJwtFormat(token)) {
                        return token.trim();
                    }
                }
            }
        }
        return null;
    }

    // Kiểm tra định dạng JWT cơ bản (phải có 2 dấu chấm)
    private boolean isValidJwtFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        String trimmed = token.trim();
        long periodCount = trimmed.chars().filter(ch -> ch == '.').count();
        return periodCount == 2;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String jwt = getTokenFromRequest(request);
        final String username;

        // Kiểm tra token null hoặc rỗng
        if (jwt == null || jwt.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Kiểm tra định dạng JWT trước khi parse
        if (!isValidJwtFormat(jwt)) {
            System.err.println("Token JWT không hợp lệ: định dạng không đúng");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            username = jwtService.extractUsername(jwt);
            System.out.println("Ngọc thu1");
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println(userDetails);
                System.out.println("Ngọc thu2");
                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    System.out.println("Ngọc thu3");
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    System.out.println("Ngọc thu4");
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Ngọc thu5");
                }
            }
        } catch (Exception e) {
            // SỬA LẠI: Thêm log để xem lỗi
            System.err.println("Lỗi xác thực JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}