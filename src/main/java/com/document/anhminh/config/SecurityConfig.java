package com.document.anhminh.config;

import com.document.anhminh.service.CustomUserDetailsService;
// SỬA LẠI: Import file filter mới
import com.document.anhminh.utils.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // SỬA LẠI: Tiêm (Inject) bộ lọc mới
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Cho phép API /auth/register
                        .requestMatchers("/auth/register").permitAll()
                        // Cho phép trang login (GET/POST) và trang register (GET/POST)
                        .requestMatchers("/login", "/register").permitAll()
                        // Tất cả yêu cầu khác phải xác thực
                        .anyRequest().permitAll()
                )
                // SỬA LẠI: Thêm bộ lọc mới
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}