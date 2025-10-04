package com.document.anhminh.service;

import com.document.anhminh.DTO.request.LoginRequest;
import com.document.anhminh.DTO.request.RegisterRequest;
import com.document.anhminh.entity.UserEntity;
import com.document.anhminh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Đăng ký người dùng mới
     */
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullname(request.getFullname())
                .build();

        userRepository.save(user);


//        UserEntity user = new UserEntity();
//        user.setUsername(request.getUsername());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setFullname(request.getFullname());

        userRepository.save(user);
        return "Đăng ký thành công";
    }

    /**
     * Đăng nhập, kiểm tra username & password, trả về JWT token
     */
    public String login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại đồ ngu"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu kìa thằng ngu");
        }

        return jwtService.generateToken(user.getUsername());
    }

    /**
     * Lấy thông tin user từ username (dùng khi muốn show profile)
     */
    public UserEntity getUserInfo(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }
}
