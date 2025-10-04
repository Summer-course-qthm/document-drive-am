package com.document.anhminh.controller;

import com.document.anhminh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    // API trả về thông tin user hiện tại dựa trên token JWT
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        // Authentication được Spring Security tự inject sau khi token hợp lệ
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserInfo(username));
    }
}